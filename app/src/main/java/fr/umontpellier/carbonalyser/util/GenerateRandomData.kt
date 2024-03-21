package fr.umontpellier.carbonalyser.util

import java.time.LocalDateTime
import kotlin.math.sin
import kotlin.random.Random

class GenerateRandomData {
    companion object {
        fun generateRandomDataForYear(year: Int): Pair<Map<LocalDateTime, Float>, Map<LocalDateTime, Float>> {
            val startDate = LocalDateTime.of(year, 1, 1, 0, 0)
            val endDate = LocalDateTime.of(year, 12, 31, 23, 0)

            val dataSent = mutableMapOf<LocalDateTime, Float>()
            val dataReceived = mutableMapOf<LocalDateTime, Float>()

            var currentDate = startDate
            while (currentDate <= endDate) {
                // Utilisez une fonction sinus pour moduler la quantité de données générées
                val modulation = sin(currentDate.monthValue.toDouble() / 12.0 * 2.0 * Math.PI) + Random.nextFloat() / 2
                val randomSent = (Random.nextFloat() + modulation).coerceIn(0.0, 10.0)
                val randomReceived = (Random.nextFloat()  + modulation).coerceIn(0.0, 10.0)

                dataSent[currentDate] = randomSent.toFloat()
                dataReceived[currentDate] = randomReceived.toFloat()

                currentDate = currentDate.plusHours(1)
            }

            return Pair(dataSent, dataReceived)
        }
    }
}