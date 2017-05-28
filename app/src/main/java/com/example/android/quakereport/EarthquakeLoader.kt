package com.example.android.quakereport

import android.content.AsyncTaskLoader
import android.content.Context


class EarthquakeLoader(context: Context, val url: String) : AsyncTaskLoader<List<Earthquake>>(context) {
    override fun loadInBackground() = QueryUtils.fetchEarthquakeData(url)

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }
}