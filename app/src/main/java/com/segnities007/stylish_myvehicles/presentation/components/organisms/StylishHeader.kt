package com.segnities007.stylish_myvehicles.presentation.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylish_myvehicles.presentation.components.atoms.StylishIconButton
import com.segnities007.stylish_myvehicles.presentation.theme.StylishMyVehiclesTheme

@Composable
fun StylishHeader(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigation: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 16.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.semantics { heading() }) { title() }
                navigation?.let {
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp)
                    ) { it() }
                }
                actions?.let {
                    Box(
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    ) { it() }
                }
            }
        }
    }
}

@Preview(name = "Stylish header", showBackground = true, widthDp = 393)
@Composable
private fun StylishHeaderPreview() {
    StylishMyVehiclesTheme {
        Surface {
            StylishHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                title = { Text("車両一覧") },
                navigation = { StylishIconButton(Icons.Default.Search, "Navigation", {}) },
                actions = { StylishIconButton(Icons.Default.Search, "Search", {}) },
            )
        }
    }
}
