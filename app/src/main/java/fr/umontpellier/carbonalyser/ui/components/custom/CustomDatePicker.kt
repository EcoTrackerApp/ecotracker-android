package fr.umontpellier.carbonalyser.ui.components.custom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun showCustomDateTimePicker(
    context: Context,
    maxDateTime: LocalDateTime = LocalDateTime.now(),
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    var selectedDate = maxDateTime.toLocalDate()
    var selectedTime = maxDateTime.toLocalTime()

    // Afficher le DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

            // Ouvrir le TimePickerDialog après avoir choisi la date
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val potentialSelectedTime = LocalTime.of(hourOfDay, minute)
                    val potentialSelectedDateTime = LocalDateTime.of(selectedDate, potentialSelectedTime)

                    if (selectedDate.isEqual(maxDateTime.toLocalDate())) {
                        val tenMinutesBeforeNow = maxDateTime.minusMinutes(10)
                        selectedTime =
                            if (potentialSelectedDateTime.isEqual(maxDateTime) || potentialSelectedDateTime.isAfter(
                                    maxDateTime
                                )
                            ) {
                                tenMinutesBeforeNow.toLocalTime()
                            } else {
                                potentialSelectedTime
                            }
                    } else {
                        selectedTime = potentialSelectedTime
                    }


                    if (potentialSelectedDateTime.isBefore(maxDateTime) || potentialSelectedDateTime.isEqual(maxDateTime)) {
                        onDateTimeSelected(LocalDateTime.of(selectedDate, selectedTime))
                    }
                },
                selectedTime.hour,
                selectedTime.minute,
                true
            )
            timePickerDialog.show()
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )
    datePickerDialog.datePicker.maxDate =
        maxDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    datePickerDialog.show()
}
