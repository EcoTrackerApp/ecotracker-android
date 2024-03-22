package fr.umontpellier.carbonalyser.util
import fr.umontpellier.carbonalyser.data.model.DataName
import fr.umontpellier.carbonalyser.data.model.DataPie
import fr.umontpellier.carbonalyser.data.model.DataType
import java.time.LocalDateTime
import java.time.Month
import kotlin.random.Random

class GenerateRandomDataPie {
    companion object {
    fun generateRandomDataForYear(): List<DataPie> {
        val randomData = mutableListOf<DataPie>()
        for (month in 1..12) {
            val daysInMonth = Month.of(month).length(false)
            for (day in 1..daysInMonth) {
                for (hour in 0..23) {
                    val date = LocalDateTime.of(2022, month, day, hour, 0)
                    val dataType = DataType.values().random()
                    val dataName = DataName.values().random()
                    val value = Random.nextFloat() * 10
                    randomData.add(DataPie(date, dataType, dataName, value))
                }
            }
        }
        return randomData
    }
}
}