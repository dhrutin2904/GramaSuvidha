package com.gramasuvidha.utils

import android.util.Log

/**
 * Centralized logging utility for Grama-Suvidha app.
 * All logs are tagged with "GramaSuvidha" for easy filtering in Logcat.
 * Usage:  AppLogger.d("YourTag", "message")
 *         AppLogger.e("YourTag", "error", exception)
 */
object AppLogger {

    private const val MASTER_TAG = "GramaSuvidha"
    private const val ENABLED = true  // Set false for production release

    fun d(tag: String, message: String) {
        if (ENABLED) Log.d("$MASTER_TAG/$tag", message)
    }

    fun i(tag: String, message: String) {
        if (ENABLED) Log.i("$MASTER_TAG/$tag", message)
    }

    fun w(tag: String, message: String) {
        if (ENABLED) Log.w("$MASTER_TAG/$tag", message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (ENABLED) {
            if (throwable != null) {
                Log.e("$MASTER_TAG/$tag", message, throwable)
            } else {
                Log.e("$MASTER_TAG/$tag", message)
            }
        }
    }

    fun v(tag: String, message: String) {
        if (ENABLED) Log.v("$MASTER_TAG/$tag", message)
    }
}
