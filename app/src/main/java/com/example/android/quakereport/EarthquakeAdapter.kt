package com.example.android.quakereport

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.android.quakereport.databinding.EarthquakeListItemBinding
import kotlin.properties.Delegates

class EarthquakeAdapter : RecyclerView.Adapter<EarthquakeViewHolder>() {
    var data by Delegates.observable(initialValue = emptyList<Earthquake>()) {
        _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = old[oldPos] == new[newPos]
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = true
        }).dispatchUpdatesTo(this)
    }

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: EarthquakeViewHolder, pos: Int) {
        holder.earthquake = data[pos]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EarthquakeListItemBinding.inflate(inflater, parent, false)
        return EarthquakeViewHolder(binding)
    }
}
