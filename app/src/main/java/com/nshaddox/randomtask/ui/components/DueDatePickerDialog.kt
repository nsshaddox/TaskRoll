package com.nshaddox.randomtask.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nshaddox.randomtask.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * A Material3 date picker dialog with Clear, Confirm, and Cancel actions.
 *
 * @param initialDate The date to pre-select in the picker. Null means no pre-selection
 *   (defaults to today in the Material3 DatePicker).
 * @param onConfirm Called with the selected [LocalDate] when Confirm is tapped,
 *   or with `null` when Clear is tapped.
 * @param onDismiss Called when Cancel is tapped or the dialog is dismissed.
 * @param modifier Modifier for customization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDatePickerDialog(
    initialDate: LocalDate?,
    onConfirm: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                initialDate
                    ?.atStartOfDay(ZoneOffset.UTC)
                    ?.toInstant()
                    ?.toEpochMilli(),
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    val selectedDate =
                        millis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        }
                    onConfirm(selectedDate)
                },
            ) {
                Text(stringResource(R.string.due_date_picker_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.due_date_picker_cancel))
            }
            TextButton(onClick = { onConfirm(null) }) {
                Text(stringResource(R.string.due_date_picker_clear))
            }
        },
        modifier = modifier,
    ) {
        DatePicker(state = datePickerState)
    }
}
