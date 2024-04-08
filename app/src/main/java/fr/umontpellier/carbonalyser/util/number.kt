package fr.umontpellier.carbonalyser.util

fun Double.format(scale: Int) = "%.${scale}f".format(this)