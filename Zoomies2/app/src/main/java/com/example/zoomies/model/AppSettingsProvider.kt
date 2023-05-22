package com.example.zoomies.model

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity

object AppSettingsProvider {
    private const val KEY_EMAIL = "Email"
    private const val DEFAULT_IS_EMAIL = true

    private lateinit var sharedPreferences: SharedPreferences

    fun initializeSharedPreferences(activity: ComponentActivity) {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
    }

    fun setIsEmailNotification(value: Boolean) {
        with (sharedPreferences.edit()) {
            putBoolean(KEY_EMAIL, value)
            apply()
        }
    }

    fun isEmailNotification(): Boolean =
        sharedPreferences.getBoolean(KEY_EMAIL, DEFAULT_IS_EMAIL)
}