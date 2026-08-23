package com.segnities007.stylish_myvehicles.presentation.screen.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.R
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.segnities007.stylishui.components.atoms.StylishIconButton
import com.segnities007.stylishui.components.atoms.StylishDialogSurface
import com.segnities007.stylishui.components.molecules.StylishConnectedCardColumn
import com.segnities007.stylishui.components.models.StylishConnectedCardItem
import com.segnities007.stylishui.components.organisms.StylishDialogActions
import com.segnities007.stylishui.components.patterns.StylishHeader
import com.segnities007.stylishui.components.patterns.StylishScaffold
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

/**
 * オープンソースライセンス画面。
 * ライブラリ情報は AboutLibraries がビルド時に Gradle 依存から収集したものを使用。
 */
@Composable
fun LicensesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val libs by rememberLibraries()
    var selected by remember { mutableStateOf<Library?>(null) }
    val libraries = remember(libs) {
        libs?.libraries?.sortedBy { it.name.lowercase() } ?: emptyList()
    }

    StylishScaffold(
        modifier = modifier,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text(stringResource(R.string.open_source_licenses)) },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back),
                        onClick = onNavigateBack,
                    )
                },
            )

            StylishConnectedCardColumn(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                spacing = 4.dp,
                items = libraries.map { library ->
                    StylishConnectedCardItem(
                        title = library.name,
                        supportingText = buildSupportingLines(library).joinToString("\n"),
                        onClick = { selected = library },
                        trailingContent = {
                            Text(
                                licenseLabel(library) ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                    )
                },
            )
        }
    }

    selected?.let { library ->
        LicenseDetailDialog(library = library, onDismiss = { selected = null })
    }
}

private fun buildSupportingLines(library: Library): List<String> {
    val parts = mutableListOf<String>()
    library.artifactVersion?.let { parts.add("v$it") }
    val author = library.organization?.name
        ?: library.developers.firstOrNull()?.name
    author?.let { parts.add(it) }
    return parts.toList()
}

private fun licenseLabel(library: Library): String? =
    library.licenses.firstOrNull()
        ?.let { it.spdxId ?: it.name }

@Composable
private fun LicenseDetailDialog(
    library: Library,
    onDismiss: () -> Unit,
) {
    StylishDialogSurface(onDismiss = onDismiss) {
        Column(Modifier.padding(24.dp)) {
            Text(library.name, style = MaterialTheme.typography.titleLarge)
            library.artifactVersion?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.version_format, it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val author = library.organization?.name
                ?: library.developers.firstOrNull()?.name
            author?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            library.description.takeIf { !it.isNullOrBlank() }
                ?.let { desc ->
                    Spacer(Modifier.height(12.dp))
                    Text(desc, style = MaterialTheme.typography.bodyMedium)
                }
            val license = library.licenses.firstOrNull()
            license?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.license_format, it.spdxId ?: it.name),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            library.website.takeIf { !it.isNullOrBlank() }
                ?.let { site ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        site,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = stringResource(R.string.close),
                cancelLabel = "",
                onConfirm = onDismiss,
                onCancel = onDismiss,
            )
        }
    }
}

@Preview(name = "LicensesScreen", showBackground = true, widthDp = 393)
@Composable
private fun LicensesScreenPreview() {
    StylishMyVehiclesTheme {
        Surface(Modifier.padding(20.dp)) {
            LicensesScreen(onNavigateBack = {})
        }
    }
}
