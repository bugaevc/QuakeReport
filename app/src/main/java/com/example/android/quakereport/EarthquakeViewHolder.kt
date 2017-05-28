package com.example.android.quakereport

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.earthquake_list_item.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeViewHolder(val view: View, val context: Context): RecyclerView.ViewHolder(view) {

    var earthquake: Earthquake? = null

    init {
        view.setOnClickListener {
            earthquake?.let {
                val uri = Uri.parse(it.URL)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }
        }
    }

    fun bind(earthquake: Earthquake) {
        this.earthquake = earthquake
        view.magnitude.text = formatMagnitude(earthquake.magnitude)

        val magnitudeCircle = view.magnitude.background as GradientDrawable
        // can't use the property here
        magnitudeCircle.setColor(getMagnitudeColor(earthquake.magnitude))

        if (earthquake.location.contains(LOCATION_SEPARATOR)) {
            val parts = earthquake.location.split(LOCATION_SEPARATOR)
            view.location_offset.text = context.getString(R.string.location_template, parts[0])
            view.primary_location.text = parts[1]
        } else {
            view.location_offset.text = context.getString(R.string.near_the)
            view.primary_location.text = earthquake.location
        }

        val date = Date(earthquake.timeInMilliseconds)
        view.date.text = formatDate(date)
        view.time.text = formatTime(date)
    }

    private fun formatDate(date: Date) = SimpleDateFormat.getDateInstance().format(date)
    private fun formatTime(date: Date) = SimpleDateFormat.getTimeInstance().format(date)
    private fun formatMagnitude(magnitude: Double) = DecimalFormat("0.0").format(magnitude)

    private fun getMagnitudeColor(magnitude: Double): Int {
        val colors = intArrayOf(
                R.color.magnitude1,
                R.color.magnitude2,
                R.color.magnitude3,
                R.color.magnitude4,
                R.color.magnitude5,
                R.color.magnitude6,
                R.color.magnitude7,
                R.color.magnitude8,
                R.color.magnitude9,
                R.color.magnitude10plus
        )
        var res = magnitude.toInt()
        res = Math.min(res, 10)
        res = Math.max(res, 1)
        res = colors[res - 1]
        return ContextCompat.getColor(context, res)
    }

    companion object {
        private val LOCATION_SEPARATOR = " of "
    }
}