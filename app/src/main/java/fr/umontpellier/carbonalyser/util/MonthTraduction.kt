package fr.umontpellier.carbonalyser.util

import java.time.Month

class MonthTraduction {
    companion object {
        fun getMonthNameInFrench(month: Month): String {
            return when (month) {
                Month.JANUARY -> "janvier"
                Month.FEBRUARY -> "février"
                Month.MARCH -> "mars"
                Month.APRIL -> "avril"
                Month.MAY -> "mai"
                Month.JUNE -> "juin"
                Month.JULY -> "juillet"
                Month.AUGUST -> "août"
                Month.SEPTEMBER -> "septembre"
                Month.OCTOBER -> "octobre"
                Month.NOVEMBER -> "novembre"
                Month.DECEMBER -> "décembre"
            }
        }
    }
}