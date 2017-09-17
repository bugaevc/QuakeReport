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

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.android.quakereport.databinding.EarthquakeActivityBinding
import okhttp3.HttpUrl

class EarthquakeActivity : AppCompatActivity(),
        LifecycleRegistryOwner,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // we do not directly extend LifecycleActivity, because it extends
    // FragmentActivity, not AppCompatActivity
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle() = lifecycleRegistry

    private lateinit var binding: EarthquakeActivityBinding
    private lateinit var viewModel: EarthquakeViewModel
    private lateinit var prefs: SharedPreferences
    private val adapter = EarthquakeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.earthquake_activity)

        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        // prefs need to be initialized before calling buildURL()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)

        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        if (viewModel.url == null) {
            viewModel.url = buildURL()
        }

        viewModel.earthquakes.observe(this, Observer {
            if (it == null) {
                return@Observer
            }

            binding.swipeRefresh.isRefreshing = it.loading
            binding.message = when {
                // TODO: what if it failed for another reason?
                it.failed -> getString(R.string.no_internet_connection)
                it.res == null || it.res.isEmpty() -> getString(R.string.no_earthquakes)
                else -> null
            }
            adapter.data = it.res ?: emptyList()
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reload()
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
        viewModel.url = buildURL()
    }

    private fun buildURL(): HttpUrl {
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
        return HttpUrl.Builder()
                .scheme("https")
                .host("earthquake.usgs.gov")
                .addPathSegments("fdsnws/event/1/query")
                .addQueryParameter("format", "geojson")
                .addQueryParameter("limit", entryCount)
                .addQueryParameter("minmag", minMagnitude)
                .addQueryParameter("orderby", orderBy)
                .build()
    }

}
