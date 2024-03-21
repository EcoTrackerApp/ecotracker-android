package fr.umontpellier.carbonalyser.data.model

import java.time.LocalDateTime

data class DataRecord(val date: LocalDateTime, val dataType: DataType, val dataOrigin: DataOrigin, val value: Float) {

}