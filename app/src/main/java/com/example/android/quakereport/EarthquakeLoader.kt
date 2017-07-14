package com.example.android.quakereport

import android.content.AsyncTaskLoader
import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


class EarthquakeLoader(context: Context, val url: URL) : AsyncTaskLoader<List<Earthquake>>(context) {

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): List<Earthquake> {
        val json = try {
            makeHttpRequest(url)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e)
            return emptyList()
        }
        return extractEarthquakes(json)
    }

    private fun extractEarthquakes(json: String): List<Earthquake> {
        try {
            val base = JSONObject(json)
            val arr = base.getJSONArray("features")
            return (0..arr.length() - 1).map {
                val obj = arr.getJSONObject(it).getJSONObject("properties")
                Earthquake(
                        obj.getDouble("mag"),
                        obj.getString("place"),
                        obj.getLong("time"),
                        obj.getString("url")
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun makeHttpRequest(url: URL): String {
        var connection: HttpURLConnection? = null
        var stream: InputStream? = null

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 10_000  // milliseconds
            connection.connectTimeout = 15_000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                stream = connection.inputStream
                val streamReader = InputStreamReader(stream, Charset.forName("UTF-8"))
                return streamReader.readText()
            } else {
                Log.e(LOG_TAG, "Error response code: " + connection.responseCode)
                return ""
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e)
            return ""
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
            if (stream != null) {
                stream.close()
            }
        }
    }

    companion object {
        private val LOG_TAG = EarthquakeLoader::class.java.simpleName
    }
}