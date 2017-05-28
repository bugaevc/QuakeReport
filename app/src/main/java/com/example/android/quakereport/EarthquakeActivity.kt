/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport

import android.app.LoaderManager
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_earthquake.*

class EarthquakeActivity : AppCompatActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<List<Earthquake>> {

    val adapter = EarthquakeAdapter()
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earthquake)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        list.setHasFixedSize(true)

        reload(launching = true)

        swipe_refresh.setOnRefreshListener {
            reload(launching = false)
        }
    }

    private fun reload(launching: Boolean) {
        empty_view.visibility = View.GONE
        if (launching) {
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this)
        } else {
            loaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, this)
        }
    }

    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        reload(launching = false)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<List<Earthquake>>? {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            swipe_refresh.isRefreshing = false
            adapter.clear()
            empty_view.setText(R.string.no_internet_connection)
            empty_view.visibility = View.VISIBLE
            return null
        }

        swipe_refresh.isRefreshing = true

        val minMagnitude = prefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        )
        val orderBy = prefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        )
        val entryCount = prefs.getString(
                getString(R.string.settings_entry_count_key),
                getString(R.string.settings_entry_count_default)
        )
        val uri = Uri.parse(USGS_REQUEST_URL).buildUpon()
                .appendQueryParameter("format", "geojson")
                .appendQueryParameter("limit", entryCount)
                .appendQueryParameter("minmag", minMagnitude)
                .appendQueryParameter("orderby", orderBy)
                .toString()
        return EarthquakeLoader(this, uri)
    }

    override fun onLoadFinished(loader: Loader<List<Earthquake>>, earthquakes: List<Earthquake>) {
        swipe_refresh.isRefreshing = false
        adapter.setData(earthquakes)
        if (earthquakes.isEmpty()) {
            empty_view.setText(R.string.no_earthquakes)
            empty_view.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.GONE
        }
    }

    override fun onLoaderReset(loader: Loader<List<Earthquake>>) = adapter.clear()

    companion object {
        val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query"
        val LOG_TAG = EarthquakeActivity::class.java.name
        val EARTHQUAKE_LOADER_ID = 1
    }
}
