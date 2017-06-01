package com.example.android.quakereport

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.android.quakereport.databinding.EarthquakeListItemBinding

class EarthquakeAdapter: RecyclerView.Adapter<EarthquakeViewHolder>() {
    var data = emptyList<Earthquake>()
        set(l) {
            field = l
            notifyDataSetChanged()
        }

    fun clear() {
        data = emptyList()
    }

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: EarthquakeViewHolder, pos: Int) = holder.bind(data[pos])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EarthquakeListItemBinding.inflate(inflater, parent, false)
        binding.context = parent.context
        return EarthquakeViewHolder(binding)
    }
}
