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
        _, old, new -> if (old != new) forceReload()
    }

    companion object {
        private val client = OkHttpClient()
    }

    fun forceReload() {
        val url = this.url
        if (url == null) {
            data.value = null
            return
        }
        val connMgr = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            data.value = LoadStatus.Failed(reloading = false)
            return
        }

        val curValue = data.value
        data.value = when (curValue) {
            null -> LoadStatus.Fine(emptyList(), reloading = true)
            is LoadStatus.Fine -> LoadStatus.Fine(curValue.res, reloading = true)
            is LoadStatus.Failed -> LoadStatus.Failed(reloading = true)
        }

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            // this runs on a different thread, so we must use postValue()
            override fun onFailure(call: Call, e: IOException) = data.postValue(
                    LoadStatus.Failed(reloading = false)
            )
            override fun onResponse(call: Call, response: Response) {
                val res = extractEarthquakes(response.body()!!.string())
                data.postValue(LoadStatus.Fine(res, reloading = false))
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
}
