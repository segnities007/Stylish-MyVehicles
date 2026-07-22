package com.segnities007.stylish_myvehicles.presentation.components.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StylishDatePickerField(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "日付を選択",
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
    ) {
        OutlinedTextField(
            value = value?.format(dateFormatter) ?: "",
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true,
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onValueChange(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        )
                    }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("キャンセル") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(name = "Stylish date picker field", showBackground = true, widthDp = 393)
@Composable
private fun StylishDatePickerFieldPreview() {
    MaterialTheme {
        StylishDatePickerField(
            value = LocalDate.now(),
            onValueChange = {},
            label = "日付",
        )
    }
}

@Preview(name = "Stylish date picker field (null)", showBackground = true, widthDp = 393)
@Composable
private fun StylishDatePickerFieldNullPreview() {
    MaterialTheme {
        StylishDatePickerField(
            value = null,
            onValueChange = {},
            label = "日付",
        )
    }
}
