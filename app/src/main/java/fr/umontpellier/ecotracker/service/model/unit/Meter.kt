package fr.umontpellier.ecotracker.service.model.unit

private const val CO2_PER_CAR_M =
    97 / 1000.0 // Conducteur moyen francais: https://carlabelling.ademe.fr/chiffrescles/r/evolutionTauxCo2#:~:text=Elle%20se%20situe%20%C3%A0%2097,tous%2Dterrains%20%C2%BB%20toujours%20constant.
private const val CO2_PER_TGV_M =
    2.4 / 1000.0 // https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports
private const val CO2_PER_PLANE_M =
    225 / 1000.0 //  https://greenly.earth/fr-fr/blog/actualites-ecologie/empreinte-carbone-comparatif-transports

@JvmInline
value class Meter(val value: Double) {

    override fun toString(): String {
        return "%.2f km".format(value / 1000.0)
    }

}