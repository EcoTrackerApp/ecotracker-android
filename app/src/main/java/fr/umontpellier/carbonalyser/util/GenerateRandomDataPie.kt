package fr.umontpellier.carbonalyser.util
import fr.umontpellier.carbonalyser.data.model.DataName
import fr.umontpellier.carbonalyser.data.model.DataPie
import fr.umontpellier.carbonalyser.data.model.DataType
import java.time.LocalDateTime
import java.time.Month
import kotlin.random.Random

class GenerateRandomDataPie {
    companion object {
        fun generateRandomDataForYears(): List<DataPie> {
            val randomData = mutableListOf<DataPie>()
            val now = LocalDateTime.now()
            for (year in 2022..now.year) {
                val endMonth = if (year == now.year) now.monthValue else 12
                for (month in 1..endMonth) {
                    val daysInMonth = if (year == now.year && month == now.monthValue) now.dayOfMonth else Month.of(month).length(false)
                    for (day in 1..daysInMonth) {
                        val endHour = if (year == now.year && month == now.monthValue && day == now.dayOfMonth) now.hour else 23
                        for (hour in 0..endHour) {
                            val date = LocalDateTime.of(year, month, day, hour, 0)
                            val dataType = DataType.values().random()
                            val dataName = DataName.values().random()
                            val value = Random.nextFloat() * 10
                            randomData.add(DataPie(date, dataType, dataName, value))
                        }
                    }
                }
            }
            return randomData
        }
    }
}