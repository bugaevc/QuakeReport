package com.example.android.quakereport

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
    }

    class EarthquakePreferenceFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_main)

            val allPrefs = listOf(
                    R.string.settings_min_magnitude_key,
                    R.string.settings_order_by_key,
                    R.string.settings_entry_count_key
            ).map { findPreference(getString(it)) }

            for (p in allPrefs) {
                bindPreferenceSummaryToValue(p)
            }
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
