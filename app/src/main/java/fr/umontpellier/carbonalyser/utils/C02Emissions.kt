package fr.umontpellier.carbonalyser.utils


private const val CO2_PER_CAR_KM = 97 // Conducteur moyen francais: https://carlabelling.ademe.fr/chiffrescles/r/evolutionTauxCo2#:~:text=Elle%20se%20situe%20%C3%A0%2097,tous%2Dterrains%20%C2%BB%20toujours%20constant.
private const val CO2_PER_TGV_KM = 2.4 // https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports
private const val CO2_PER_PLANE_KM = 225 //  https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports

@JvmInline
value class CO2Emissions(val amount: Double) {
    fun toCarKilometers(): Double {
        return amount/ CO2_PER_CAR_KM
    }
    fun toTrainKilometers(): Double {
        return amount/ CO2_PER_TGV_KM
    }
    fun toPlaneKilometers(): Double {
        return amount/ CO2_PER_PLANE_KM
    }

}