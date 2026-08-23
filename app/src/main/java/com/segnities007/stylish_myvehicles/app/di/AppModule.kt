package com.segnities007.stylish_myvehicles.app.di

import com.segnities007.stylish_myvehicles.data.local.AppDatabase
import com.segnities007.stylish_myvehicles.data.repository.CostRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.FuelRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.MaintenanceRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.MaintenanceScheduleRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.SettingsRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.TripRecordRepositoryImpl
import com.segnities007.stylish_myvehicles.data.repository.VehicleRepositoryImpl
import com.segnities007.stylish_myvehicles.domain.repository.CostRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.FuelRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.MaintenanceScheduleRepository
import com.segnities007.stylish_myvehicles.domain.repository.SettingsRepository
import com.segnities007.stylish_myvehicles.domain.repository.TripRecordRepository
import com.segnities007.stylish_myvehicles.domain.repository.VehicleRepository
import com.segnities007.stylish_myvehicles.domain.usecase.fuel.InsertFuelRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.maintenance.InsertMaintenanceRecordUseCase
import com.segnities007.stylish_myvehicles.domain.usecase.vehicle.InsertVehicleUseCase
import com.segnities007.stylish_myvehicles.presentation.screen.cost.CostListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.fuel.FuelRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.maintenance.MaintenanceRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.notification.NotificationViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.records.RecordsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.recordslist.RecordsListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.settings.SettingsViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.trip.TripRecordViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehicledetail.VehicleDetailViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehicleedit.VehicleEditViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclelist.VehicleListViewModel
import com.segnities007.stylish_myvehicles.presentation.screen.vehiclepager.VehiclePagerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().fuelRecordDao() }
    single { get<AppDatabase>().maintenanceRecordDao() }
    single { get<AppDatabase>().costRecordDao() }
    single { get<AppDatabase>().maintenanceScheduleDao() }
    single { get<AppDatabase>().tripRecordDao() }

    // Repository
    single<VehicleRepository> { VehicleRepositoryImpl(get()) }
    single<FuelRecordRepository> { FuelRecordRepositoryImpl(get()) }
    single<MaintenanceRecordRepository> { MaintenanceRecordRepositoryImpl(get()) }
    single<MaintenanceScheduleRepository> { MaintenanceScheduleRepositoryImpl(get()) }
    single<CostRecordRepository> { CostRecordRepositoryImpl(get()) }
    single<TripRecordRepository> { TripRecordRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }

    // UseCase (cross-aggregate orchestration only)
    single { InsertVehicleUseCase(get(), get()) }
    single { InsertFuelRecordUseCase(get(), get()) }
    single { InsertMaintenanceRecordUseCase(get(), get(), get()) }

    // ViewModel
    viewModel { VehicleListViewModel(get()) }
    viewModel {
        VehiclePagerViewModel(
            vehicleRepository = get(),
            costRecordRepository = get(),
            fuelRecordRepository = get(),
            maintenanceScheduleRepository = get(),
        )
    }
    viewModel { params ->
        VehicleDetailViewModel(
            vehicleId = params.get(),
            vehicleRepository = get(),
            fuelRecordRepository = get(),
            maintenanceRecordRepository = get(),
            costRecordRepository = get(),
        )
    }
    viewModel { params ->
        VehicleEditViewModel(
            vehicleId = params.getOrNull(),
            vehicleRepository = get(),
            insertVehicleUseCase = get(),
        )
    }
    viewModel { params ->
        FuelRecordViewModel(
            vehicleId = params.get(),
            fuelRecordRepository = get(),
            insertFuelRecordUseCase = get(),
        )
    }
    viewModel { params ->
        MaintenanceRecordViewModel(
            vehicleId = params.get(),
            maintenanceRecordRepository = get(),
            insertMaintenanceRecordUseCase = get(),
            vehicleRepository = get(),
            maintenanceScheduleRepository = get(),
        )
    }
    viewModel { params ->
        CostListViewModel(
            vehicleId = params.get(),
            costRecordRepository = get(),
        )
    }
    viewModel { params ->
        RecordsViewModel(
            vehicleId = params.get(),
            topic = params.get(),
            initialPeriodMode = params.get(),
            fuelRecordRepository = get(),
            maintenanceRecordRepository = get(),
            costRecordRepository = get(),
            vehicleRepository = get(),
        )
    }
    viewModel {
        NotificationViewModel(
            vehicleRepository = get(),
            maintenanceScheduleRepository = get(),
        )
    }
    viewModel {
        RecordsListViewModel(
            vehicleRepository = get(),
            fuelRecordRepository = get(),
            maintenanceRecordRepository = get(),
            costRecordRepository = get(),
            tripRecordRepository = get(),
        )
    }
    viewModel { params ->
        TripRecordViewModel(
            vehicleId = params.get(),
            tripRecordRepository = get(),
        )
    }
    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
        )
    }
}
