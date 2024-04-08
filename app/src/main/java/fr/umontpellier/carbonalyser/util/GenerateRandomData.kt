package fr.umontpellier.carbonalyser.util

import fr.umontpellier.carbonalyser.model.ModelResult
import fr.umontpellier.carbonalyser.ui.components.graph.DataOrigin
import fr.umontpellier.carbonalyser.ui.components.graph.DataType

class GenerateRandomData {
    companion object {
        fun generateRandomDataForYear(): List<ModelResult> {
            /**val startDate = LocalDateTime.of(2022, 1, 1, 0, 0)
            val endDate = LocalDateTime.now()

            val dataRecords = mutableListOf<DataRecord>()

            var currentDate = startDate
            while (currentDate <= endDate) {
            val dataType = when (Random.nextInt(10)) {
            in 0..3 -> DataType.WIFI
            in 4..10 -> DataType.MOBILE_DATA
            else -> DataType.CONNECTION_SHARING
            }

            val randomSent = generateRandomValue(dataType, DataOrigin.SEND, currentDate.dayOfMonth)
            val randomReceived = generateRandomValue(dataType, DataOrigin.RECEIVED, currentDate.dayOfMonth)

            dataRecords.add(DataRecord(currentDate, dataType, DataOrigin.SEND, randomSent))
            dataRecords.add(DataRecord(currentDate, dataType, DataOrigin.RECEIVED, randomReceived))

            currentDate = currentDate.plusHours(1)
            }

            return dataRecords**/
            return emptyList()
        }

        private fun generateRandomValue(dataType: DataType, dataOrigin: DataOrigin, dayOfMonth: Int): Float {
            /**val range = when (dataType) {
            DataType.WIFI -> if (dataOrigin == DataOrigin.SEND) 1.0 else 2.0
            DataType.MOBILE_DATA -> if (dataOrigin == DataOrigin.SEND) 0.1 else 0.4
            else -> 0.0
            }
            var randomValue = Random.nextDouble() * range // Génère une valeur aléatoire dans la plage spécifiée
            randomValue = if (dayOfMonth % Random.nextInt(
            1,
            6
            ) == 0
            ) 0.0 else randomValue // Si le jour du mois est divisible par un nombre aléatoire entre 1 et 5, la valeur est définie à 0
            return randomValue.toFloat()**/
            return 0F
        }
    }
}