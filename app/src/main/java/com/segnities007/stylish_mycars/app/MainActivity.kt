package com.segnities007.stylish_mycars.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.segnities007.stylish_mycars.R
import com.segnities007.stylish_mycars.domain.usecase.ExportDocument
import com.segnities007.stylish_mycars.presentation.navigation.AppNavigation
import com.segnities007.stylish_mycars.presentation.theme.OnboardingPreference
import com.segnities007.stylish_mycars.presentation.theme.StylishMyCarsTheme
import com.segnities007.stylish_mycars.presentation.theme.ThemePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var themeMode by remember { mutableStateOf(ThemePreference.getThemeMode(this)) }

            StylishMyCarsTheme(themeMode = themeMode) {
                var pendingDocument by remember { mutableStateOf<ExportDocument?>(null) }
                val showOnboarding = remember { !OnboardingPreference.isCompleted(this) }
                val exportLauncher = rememberLauncherForActivityResult(
                    CreateExportDocumentContract(),
                ) { uri ->
                    val document = pendingDocument
                    pendingDocument = null
                    if (uri != null && document != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val result = runCatching {
                                contentResolver.openOutputStream(uri)
                                    ?.bufferedWriter()
                                    ?.use {
                                        it.write(document.content)
                                    } ?: error("Unable to open export destination")
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    if (result.isSuccess) R.string.export_saved
                                    else R.string.export_failed,
                                    Toast.LENGTH_LONG,
                                )
                                    .show()
                            }
                        }
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    AppNavigation(
                        showOnboarding = showOnboarding,
                        onSaveDocument = { document ->
                            pendingDocument = document
                            exportLauncher.launch(document)
                        },
                        onThemeChanged = { mode -> themeMode = mode },
                        onOnboardingComplete = { OnboardingPreference.setCompleted(this@MainActivity) },
                    )
                    SystemBarScrims()
                }
            }
        }
    }
}

private class CreateExportDocumentContract :
    ActivityResultContract<ExportDocument, Uri?>() {
    override fun createIntent(context: Context, input: ExportDocument): Intent =
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = input.mimeType
            putExtra(Intent.EXTRA_TITLE, input.fileName)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        if (resultCode == Activity.RESULT_OK) intent?.data else null
}

@Composable
private fun BoxScope.SystemBarScrims() {
    val scrim = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
    val topHeight = WindowInsets.statusBars.asPaddingValues()
        .calculateTopPadding()
    val bottomHeight = WindowInsets.navigationBars.asPaddingValues()
        .calculateBottomPadding()

    Box(
        Modifier
            .fillMaxWidth()
            .height(topHeight)
            .align(Alignment.TopCenter)
            .background(scrim),
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(bottomHeight)
            .align(Alignment.BottomCenter)
            .background(scrim),
    )
}
