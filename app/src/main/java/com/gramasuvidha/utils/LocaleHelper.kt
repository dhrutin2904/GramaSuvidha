package com.gramasuvidha.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Helper class for managing app language/locale switching.
 * Supports English (en) and Kannada (kn).
 */
object LocaleHelper {

    private const val PREF_NAME = "locale_prefs"
    private const val KEY_LANGUAGE = "app_language"

    /**
     * Sets the app language and saves the preference.
     * @param context Application context
     * @param languageCode "en" for English, "kn" for Kannada
     */
    fun setLocale(context: Context, languageCode: String) {
        saveLanguage(context, languageCode)
        updateResources(context, languageCode)
    }

    /**
     * Applies the saved locale setting to the context.
     * Should be called in Application.onCreate() and Activity.attachBaseContext().
     */
    fun applyLocale(context: Context) {
        val language = getLanguage(context)
        updateResources(context, language)
    }

    /**
     * Wraps the context with the saved locale for proper resource loading.
     * Call this in Activity.attachBaseContext().
     */
    fun wrapContext(context: Context): Context {
        val language = getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    /**
     * Returns the currently saved language code.
     */
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    /**
     * Checks if the current language is Kannada.
     */
    fun isKannada(context: Context): Boolean {
        return getLanguage(context) == "kn"
    }

    /**
     * Toggles between English and Kannada.
     * @return The new language code after toggle.
     */
    fun toggleLanguage(context: Context): String {
        val current = getLanguage(context)
        val newLang = if (current == "en") "kn" else "en"
        setLocale(context, newLang)
        return newLang
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    private fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
