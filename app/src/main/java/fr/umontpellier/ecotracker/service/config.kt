package fr.umontpellier.ecotracker.service

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
data class EcoTrackerConfig(
    /**
     * L'intervalle sur laquelle les analyses de l'application sont faites.
     */
    var dates: Pair<Instant, Instant> =
        now().minus(7, ChronoUnit.DAYS) to now(),
    /**
     * La précision sur laquelle on analyse
     */
    var precision: Duration = Duration.ofDays(1),
    /**
     * Le modèle utilisé dans l'application
     */
    var model: String = "1byte",
    /**
     * Les configurations spécifiques aux applications.
     */
    val apps: MutableMap<Int, AppConfig> = mutableMapOf()
) {

    /**
     * Configuration spécifique à chaque application.
     */
    data class AppConfig(var isGreen: Boolean = false)

}