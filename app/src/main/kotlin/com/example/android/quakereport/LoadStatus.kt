package com.example.android.quakereport

sealed class LoadStatus<out T>(val reloading: Boolean) {
    class Fine<out T>(val res: T, reloading: Boolean) : LoadStatus<T>(reloading)
    class Failed<out T>(reloading: Boolean) : LoadStatus<T>(reloading)
}