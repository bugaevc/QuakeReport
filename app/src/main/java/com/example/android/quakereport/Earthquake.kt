package com.example.android.quakereport

import android.content.Context
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Earthquake(var magnitude: Double, var location: String, var timeInMilliseconds: Long, var URL: String) {

    private fun formatDate(date: Date) = SimpleDateFormat.getDateInstance().format(date)
    private fun formatTime(date: Date) = SimpleDateFormat.getTimeInstance().format(date)
    fun formatMagnitude(): String = DecimalFormat("0.0").format(magnitude)

    val date: String
        get() = formatDate(Date(timeInMilliseconds))
    val time: String
        get() = formatTime(Date(timeInMilliseconds))

    data class Location(val primary: String, val offset: String)

    fun getFancyLocation(context: Context): Location {
        val LOCATION_SEPARATOR = " of "
        if (location.contains(LOCATION_SEPARATOR)) {
            val parts = location.split(LOCATION_SEPARATOR)
            return Location(
                    offset = context.getString(R.string.location_template, parts[0]),
                    primary = parts[1]
            )
        } else {
            return Location(
                    offset = context.getString(R.string.near_the),
                    primary = location
            )
        }
    }
}
