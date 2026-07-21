package com.segnities007.stylish_mycars.presentation.screen.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.segnities007.stylish_mycars.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedColumnCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedShape
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishConnectedCard
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogActions
import com.segnities007.stylish_mycars.presentation.components.molecules.StylishDialogSurface
import com.segnities007.stylish_mycars.presentation.components.organisms.StylishHeader

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

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("オープンソースライセンス") },
                navigation = {
                    StylishIconButton(
                        Icons.AutoMirrored.Filled.ArrowBack, "戻る",
                        onClick = onNavigateBack,
                    )
                },
            )

            LazyColumn(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(libraries) { index, library ->
                    StylishConnectedCard(
                        title = library.name,
                        supportingText = buildSupportingText(library),
                        onClick = { selected = library },
                        onLongClick = {},
                        shape = stylishConnectedShape(
                            stylishConnectedColumnCorners(index, libraries.size),
                        ),
                        trailingContent = {
                            Text(
                                licenseLabel(library) ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        },
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    selected?.let { library ->
        LicenseDetailDialog(library = library, onDismiss = { selected = null })
    }
}

private fun buildSupportingText(library: Library): String {
    val parts = mutableListOf<String>()
    library.artifactVersion?.let { parts.add("v$it") }
    val author = library.organization?.name
        ?: library.developers.firstOrNull()?.name
    author?.let { parts.add(it) }
    return parts.joinToString(" / ")
}

private fun licenseLabel(library: Library): String? =
    library.licenses.firstOrNull()?.let { it.spdxId ?: it.name }

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
                    "バージョン $it",
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
            library.description.takeIf { !it.isNullOrBlank() }?.let { desc ->
                Spacer(Modifier.height(12.dp))
                Text(desc, style = MaterialTheme.typography.bodyMedium)
            }
            val license = library.licenses.firstOrNull()
            license?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    "ライセンス: ${it.spdxId ?: it.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            library.website.takeIf { !it.isNullOrBlank() }?.let { site ->
                Spacer(Modifier.height(4.dp))
                Text(
                    site,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(24.dp))
            StylishDialogActions(
                confirmLabel = "閉じる",
                cancelLabel = "",
                onConfirm = onDismiss,
                onCancel = onDismiss,
            )
        }
    }
}
