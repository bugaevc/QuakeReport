package com.example.android.quakereport

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.example.android.quakereport.databinding.EarthquakeListItemBinding
import kotlin.properties.Delegates

class EarthquakeViewHolder(private val binding: EarthquakeListItemBinding): RecyclerView.ViewHolder(binding.root) {

    var earthquake by Delegates.observable(initialValue = null as Earthquake?) {
        _, _, value ->
        binding.earthquake = value
        binding.executePendingBindings()

        if (value != null) {
            val magnitudeCircle = binding.magnitude.background as GradientDrawable
            magnitudeCircle.setColor(getMagnitudeColor(value.magnitude))
        }
    }

    init {
        binding.root.setOnClickListener {
            earthquake?.apply {
                val uri = Uri.parse(URL)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                binding.root.context.startActivity(intent)
            }
        }
    }

    private fun getMagnitudeColor(magnitude: Double): Int {
        val res = when (magnitude.coerceAtLeast(1.0).toInt()) {
            1 -> R.color.magnitude1
            2 -> R.color.magnitude2
            3 -> R.color.magnitude3
            4 -> R.color.magnitude4
            5 -> R.color.magnitude5
            6 -> R.color.magnitude6
            7 -> R.color.magnitude7
            8 -> R.color.magnitude8
            9 -> R.color.magnitude9
            else -> R.color.magnitude10plus
        }
        return ContextCompat.getColor(binding.root.context, res)
    }
}