package fr.umontpellier.ecotracker.ui.util

import java.time.ZoneId
import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    .withZone(ZoneId.systemDefault())