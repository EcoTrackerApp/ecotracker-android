package fr.umontpellier.carbonalyser.data.model

import java.time.LocalDateTime

data class DataPie(val date: LocalDateTime, val dataType: DataType, val dataName: DataName, val value: Float) {


}