package fr.umontpellier.ecotracker.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.umontpellier.ecotracker.service.EcoTrackerConfig
import fr.umontpellier.ecotracker.service.netstat.PkgNetStatService
import org.koin.compose.koinInject
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDateDialog(
    config: EcoTrackerConfig = koinInject(),
    pkgNetStatService: PkgNetStatService = koinInject(),
    showDialog: Boolean = true,
    onClose: () -> Unit = {},
) {
    val start = rememberDatePickerState(config.dates.first.toEpochMilli())
    val end = rememberDatePickerState(config.dates.second.toEpochMilli())

    var duration by remember { mutableStateOf(TextFieldValue(config.precision.toDays().toString())) }
    var durationUnit by remember { mutableStateOf("Jours") }

    val dropdownItems = listOf("Jours", "Semaines", "Mois")

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = onClose,
            content = {
                Card {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        DatePicker(title = { Text("Début") }, state = start)
                        DatePicker(title = { Text("Fin") }, state = end)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Durée") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        var expanded by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            OutlinedTextField(
                                value = durationUnit,
                                onValueChange = { /* Ignore */ },
                                label = { Text("Unité de Durée") },
                                singleLine = true,
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = Color.Gray
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                dropdownItems.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            durationUnit = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            TextButton(
                                onClick = onClose,
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Annuler")
                            }
                            TextButton(
                                onClick = {
                                    if (start.selectedDateMillis != null && end.selectedDateMillis != null) {
                                        config.dates =
                                            Instant.ofEpochMilli(start.selectedDateMillis!!) to Instant.ofEpochMilli(end.selectedDateMillis!!)
                                    }
                                    val d = when (durationUnit) {
                                        "Semaines" -> Duration.ofDays(7 * duration.text.toLong())
                                        "Mois" -> Duration.ofDays(31 * duration.text.toLong())
                                        else -> Duration.ofDays(duration.text.toLong())
                                    }
                                    config.precision = d
                                    pkgNetStatService.fetchAndCache()
                                    onClose()
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Sauvegarder")
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun ChangeDateDialogPreview() {
    ChangeDateDialog()
}