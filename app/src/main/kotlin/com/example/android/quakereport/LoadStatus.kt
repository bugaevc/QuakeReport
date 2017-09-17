package com.example.android.quakereport

data class LoadStatus<out T>(
        val res: T? = null,
        val loading: Boolean = false,
        val failed: Boolean = false
)