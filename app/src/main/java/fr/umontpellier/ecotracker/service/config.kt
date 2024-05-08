package fr.umontpellier.ecotracker.service

import fr.umontpellier.ecotracker.serialization.DurationSerializer
import fr.umontpellier.ecotracker.serialization.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit

/**
 * Représente la configuration globale du projet.
 *
 * Il est possible à partir de la configuration globale, d'accéder à la configuration
 * par application à l'aide de [EcoTrackerConfig.apps], pour des questions d'optimisation, cet
 * accès se fait avec [les UIDs](https://developer.android.com/reference/android/os/Process#myUid%28%29)
 */
@Serializable
data class EcoTrackerConfig(
    /**
     * L'intervalle sur laquelle les analyses de l'application sont faites.
     */
    var dates: Pair<@Serializable(with = InstantSerializer::class) Instant, @Serializable(with = InstantSerializer::class) Instant> =
        now().minus(7, ChronoUnit.DAYS) to now(),
    /**
     * La précision sur laquelle on analyse
     */
    @Serializable(with = DurationSerializer::class)
    var precision: Duration = Duration.ofDays(1),
    /**
     * Le modèle utilisé dans l'application
     */
    var model: String = "swd",
    /**
     * Les configurations spécifiques aux applications.
     */
    val apps: MutableMap<Int, AppConfig> = mutableMapOf(),
    /**
     * Application que l'on est en train de configurer
     */
    var currentApp: Int? = null
) {

    /**
     * Configuration spécifique à chaque application.
     */
    @Serializable
    data class AppConfig(var isGreen: Boolean = false)

}