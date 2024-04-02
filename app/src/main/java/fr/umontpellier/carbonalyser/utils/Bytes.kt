package fr.umontpellier.carbonalyser.utils
import fr.umontpellier.carbonalyser.model.Model
import fr.umontpellier.carbonalyser.model.ModelOptions

@JvmInline
value class Bytes(val value: Long) {
    fun toCO2Emissions(model: Model, option:Boolean): CO2Emissions {
        return model.convert(this, option);
    }


}
