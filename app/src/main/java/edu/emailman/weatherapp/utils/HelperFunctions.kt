package edu.emailman.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getFormattedDate(dt: Number, pattern: String = "dd/MM/yyyy"): String {
    return SimpleDateFormat(
        pattern,
        Locale.getDefault()
    ).format(Date(dt.toLong() * 1000))
}

fun getIconUrl(icon: String): String {
    return "https://openweathermap.org/img/wn/$icon@2x.png"
}

fun getWindDirection(deg: Number?): String {
    return when (deg?.toInt()) {
        in 0..22 -> "N"
        in 338..359 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"

        else -> "XX"
    }
}