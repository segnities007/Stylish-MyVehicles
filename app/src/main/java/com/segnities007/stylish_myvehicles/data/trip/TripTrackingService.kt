package com.segnities007.stylish_myvehicles.data.trip

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.segnities007.stylish_myvehicles.R
import com.segnities007.stylish_myvehicles.app.MainActivity
import com.segnities007.stylish_myvehicles.data.local.AppDatabase
import com.segnities007.stylish_myvehicles.data.local.entity.TripLocationPointEntity
import com.segnities007.stylish_myvehicles.data.local.entity.TripRecordEntity
import com.segnities007.stylish_myvehicles.domain.model.TripPurpose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.roundToLong

class TripTrackingService : Service(), LocationListener {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationManager: LocationManager
    private lateinit var database: AppDatabase
    private val preferences by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private var activeTripId: Long = 0
    private var distanceMeters: Double = 0.0
    private var lastLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.create(applicationContext)
        locationManager = getSystemService(LocationManager::class.java)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, trackingNotification())
        return when (intent?.action) {
            ACTION_START -> {
                startTrip(intent.getLongExtra(EXTRA_VEHICLE_ID, 0))
                START_STICKY
            }
            ACTION_STOP -> {
                finishTrip()
                START_NOT_STICKY
            }
            else -> {
                resumeTrip()
                START_STICKY
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startTrip(vehicleId: Long) {
        if (vehicleId <= 0 || preferences.getLong(KEY_TRIP_ID, 0) > 0) {
            resumeTrip()
            return
        }
        serviceScope.launch {
            activeTripId = database.tripRecordDao().insert(
                TripRecordEntity(
                    vehicleId = vehicleId,
                    title = "",
                    purpose = TripPurpose.DRIVE.name,
                    startedAt = LocalDateTime.now(),
                    endedAt = null,
                    distanceMeters = 0,
                    startOdometer = null,
                    endOdometer = null,
                    memo = "",
                ),
            )
            preferences.edit()
                .putLong(KEY_TRIP_ID, activeTripId)
                .putLong(KEY_VEHICLE_ID, vehicleId)
                .putLong(KEY_DISTANCE_METERS, 0)
                .apply()
            requestLocationUpdates()
        }
    }

    private fun resumeTrip() {
        activeTripId = preferences.getLong(KEY_TRIP_ID, 0)
        distanceMeters = preferences.getLong(KEY_DISTANCE_METERS, 0).toDouble()
        if (activeTripId > 0) requestLocationUpdates() else stopSelf()
    }

    private fun requestLocationUpdates() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }
        runCatching {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL_MS,
                MIN_DISTANCE_METERS,
                this,
                Looper.getMainLooper(),
            )
        }.onFailure { stopSelf() }
    }

    override fun onLocationChanged(location: Location) {
        if (activeTripId <= 0 || location.accuracy > MAX_ACCURACY_METERS) return
        val previous = lastLocation
        val delta = previous?.distanceTo(location)?.toDouble() ?: 0.0
        if (delta > MAX_SINGLE_JUMP_METERS) {
            lastLocation = location
            return
        }
        if (delta >= MIN_DISTANCE_METERS) distanceMeters += delta
        lastLocation = location

        val roundedDistance = distanceMeters.roundToLong()
        preferences.edit().putLong(KEY_DISTANCE_METERS, roundedDistance).apply()
        serviceScope.launch {
            database.tripRecordDao().insertLocation(
                TripLocationPointEntity(
                    tripId = activeTripId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    recordedAt = LocalDateTime.now(),
                    accuracyMeters = location.accuracy,
                ),
            )
            database.tripRecordDao().updateDistance(activeTripId, roundedDistance)
        }
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, trackingNotification(roundedDistance))
    }

    private fun finishTrip() {
        locationManager.removeUpdates(this)
        activeTripId = preferences.getLong(KEY_TRIP_ID, activeTripId)
        distanceMeters = preferences.getLong(KEY_DISTANCE_METERS, distanceMeters.roundToLong())
            .toDouble()
        val tripId = activeTripId
        serviceScope.launch {
            if (tripId > 0) {
                database.tripRecordDao().finish(
                    tripId = tripId,
                    endedAt = LocalDateTime.now(),
                    distanceMeters = distanceMeters.roundToLong(),
                )
            }
            preferences.edit().clear().apply()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun trackingNotification(distance: Long = distanceMeters.roundToLong()): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            stopIntent(this),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("ドライブを記録中")
            .setContentText("%.1f km 走行".format(distance / 1000.0))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, "記録を終了", stopIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "ドライブ記録",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "GPSを使用したドライブ記録の状態を表示します"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        locationManager.removeUpdates(this)
        serviceScope.cancel()
        database.close()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_START = "trip.action.START"
        private const val ACTION_STOP = "trip.action.STOP"
        private const val EXTRA_VEHICLE_ID = "vehicle_id"
        private const val PREFS_NAME = "active_trip"
        private const val KEY_TRIP_ID = "trip_id"
        private const val KEY_VEHICLE_ID = "vehicle_id"
        private const val KEY_DISTANCE_METERS = "distance_meters"
        private const val CHANNEL_ID = "trip_tracking"
        private const val NOTIFICATION_ID = 2100
        private const val LOCATION_INTERVAL_MS = 5_000L
        private const val MIN_DISTANCE_METERS = 5f
        private const val MAX_ACCURACY_METERS = 50f
        private const val MAX_SINGLE_JUMP_METERS = 2_000.0

        fun startIntent(context: Context, vehicleId: Long) =
            Intent(context, TripTrackingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_VEHICLE_ID, vehicleId)
            }

        fun stopIntent(context: Context) =
            Intent(context, TripTrackingService::class.java).apply {
                action = ACTION_STOP
            }

        fun isRecording(context: Context): Boolean =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_TRIP_ID, 0) > 0

        fun recordingVehicleId(context: Context): Long? =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_VEHICLE_ID, 0)
                .takeIf { it > 0 }

        fun start(context: Context, vehicleId: Long) {
            ContextCompat.startForegroundService(context, startIntent(context, vehicleId))
        }

        fun stop(context: Context) {
            ContextCompat.startForegroundService(context, stopIntent(context))
        }
    }
}
