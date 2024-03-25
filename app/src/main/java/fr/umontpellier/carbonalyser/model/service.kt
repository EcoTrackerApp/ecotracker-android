package fr.umontpellier.carbonalyser.model

object ModelService {

    private val models = listOf<Model>(OneByte)
        .associateBy { it.id }

    /**
     * Returns a model from its [id].
     */
    operator fun get(id: String) = models[id]

}