package com.example.android.quakereport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    class EarthquakePreferenceFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_main)

            val minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key))
            bindPreferenceSummaryToValue(minMagnitude)
            val orderBy = findPreference(getString(R.string.settings_order_by_key))
            bindPreferenceSummaryToValue(orderBy)
            val entryCount = findPreference(getString(R.string.settings_entry_count_key))
            bindPreferenceSummaryToValue(entryCount)
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = this
            val prefs = PreferenceManager.getDefaultSharedPreferences(preference.context)
            val s = prefs.getString(preference.key, "")
            onPreferenceChange(preference, s)
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val str = newValue.toString()
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(str)
                if (index >= 0) {
                    preference.summary = preference.entries[index]
                }
            } else {
                preference.summary = str
            }
            return true
        }
    }
}
