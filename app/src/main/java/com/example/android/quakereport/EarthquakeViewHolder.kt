package com.example.android.quakereport

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.example.android.quakereport.databinding.EarthquakeListItemBinding

class EarthquakeViewHolder(val binding: EarthquakeListItemBinding): RecyclerView.ViewHolder(binding.root) {

    var earthquake: Earthquake? = null

    init {
        binding.root.setOnClickListener {
            earthquake?.apply {
                val uri = Uri.parse(URL)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                binding.context.startActivity(intent)
            }
        }
    }

    fun bind(earthquake: Earthquake) {
        this.earthquake = earthquake
        binding.earthquake = earthquake
        binding.executePendingBindings()

        val magnitudeCircle = binding.magnitude.background as GradientDrawable
        // can't use the property here
        magnitudeCircle.setColor(getMagnitudeColor(earthquake.magnitude))
    }

    fun getMagnitudeColor(magnitude: Double): Int {
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
        return ContextCompat.getColor(binding.context, res)
    }
}