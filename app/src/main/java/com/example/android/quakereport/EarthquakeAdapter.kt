package com.example.android.quakereport

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class EarthquakeAdapter: RecyclerView.Adapter<EarthquakeViewHolder>() {
    val data = ArrayList<Earthquake>()

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: EarthquakeViewHolder, pos: Int) = holder.bind(data[pos])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.earthquake_list_item, parent, false)
        return EarthquakeViewHolder(view, parent.context)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }
    fun setData(l: List<Earthquake>) {
        data.clear()
        data.addAll(l)
        notifyDataSetChanged()
    }
}
