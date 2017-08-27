package com.example.android.quakereport

import android.content.Context
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

data class Earthquake(
        val magnitude: Double,
        val location: String,
        private val timeInMilliseconds: Long,
        val url: String
) {

    private fun formatDate(date: Date) = SimpleDateFormat.getDateInstance().format(date)!!
    private fun formatTime(date: Date) = SimpleDateFormat.getTimeInstance().format(date)!!
    fun formatMagnitude() = DecimalFormat("0.0").format(magnitude)!!

    val date = formatDate(Date(timeInMilliseconds))
    val time = formatTime(Date(timeInMilliseconds))

    data class Location(val primary: String, val offset: String)

    fun getFancyLocation(context: Context): Location {
        val LOCATION_SEPARATOR = " of "
        return if (location.contains(LOCATION_SEPARATOR)) {
            val parts = location.split(LOCATION_SEPARATOR)
            Location(
                    offset = context.getString(R.string.location_template, parts[0]),
                    primary = parts[1]
            )
        } else {
            Location(
                    offset = context.getString(R.string.near_the),
                    primary = location
            )
        }
    }
}
