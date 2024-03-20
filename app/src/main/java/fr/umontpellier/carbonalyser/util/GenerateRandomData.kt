package fr.umontpellier.carbonalyser.util

import java.time.LocalDate
import kotlin.random.Random

class GenerateRandomData {
    companion object {
        fun generateRandomDataForYear(year: Int): Pair<Map<LocalDate, Float>, Map<LocalDate, Float>> {
            val startDate = LocalDate.of(year, 1, 1)
            val endDate = LocalDate.of(year, 12, 31)

            val dataSent = mutableMapOf<LocalDate, Float>()
            val dataReceived = mutableMapOf<LocalDate, Float>()

            var currentDate = startDate
            while (currentDate <= endDate) {
                val randomSent = Random.nextFloat() * (2 * Random.nextInt(1, currentDate.month.value + 1))
                val randomReceived = Random.nextFloat() * (4 * Random.nextInt(1, currentDate.month.value + 1))

                dataSent[currentDate] = randomSent
                dataReceived[currentDate] = randomReceived

                currentDate = currentDate.plusDays(1)
            }

            return Pair(dataSent, dataReceived)
        }
    }
}