package com.segnities007.stylishui.components.patterns

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.stylishui.theme.StylishTheme

/** セクションの見出し。プライマリカラーの中見出しテキスト。 */
@Composable
fun StylishSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 20.dp),
    )
}

@Preview(name = "Section title", showBackground = true, widthDp = 393)
@Composable
private fun StylishSectionTitlePreview() {
    StylishTheme(darkTheme = false) {
        Surface(Modifier.padding(20.dp)) {
            StylishSectionTitle("期限管理")
        }
    }
}
