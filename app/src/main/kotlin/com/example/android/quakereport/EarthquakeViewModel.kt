package com.example.android.quakereport

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.ConnectivityManager
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class EarthquakeViewModel(private val app: Application) : AndroidViewModel(app) {
    private val data = MutableLiveData<LoadStatus<List<Earthquake>>>()
    val earthquakes: LiveData<LoadStatus<List<Earthquake>>> = data

    var url by Delegates.observable(initialValue = null as HttpUrl?) {
        _, old, new -> if (old != new) reload()
    }

    companion object {
        private val client = OkHttpClient()
    }

    fun reload() {
        if (connMgr.activeNetworkInfo?.isConnected != true) {
            data.value = LoadStatus(failed = true)
            return
        }

        data.value = data.value?.copy(loading = true) ?: LoadStatus(loading = true)

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            // this runs on a different thread, so we must use postValue()
            override fun onFailure(call: Call, e: IOException) = data.postValue(
                    LoadStatus(failed = true)
            )
            override fun onResponse(call: Call, response: Response) {
                val res = extractEarthquakes(response.body()!!.string())
                data.postValue(LoadStatus(res))
            }
        })
    }

    private fun extractEarthquakes(json: String): List<Earthquake> {
        return try {
            val arr = JSONObject(json).getJSONArray("features")
            (0 until arr.length()).map {
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
            emptyList()
        }
    }

    private val connMgr by lazy {
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
