package com.gramasuvidha

import android.app.Application
import com.gramasuvidha.data.local.AppDatabase
import com.gramasuvidha.data.repository.ProjectRepository
import com.gramasuvidha.utils.AppLogger
import com.gramasuvidha.utils.LocaleHelper

/**
 * Application class for Grama-Suvidha Portal.
 * Initializes database, repository, and locale settings.
 */
class GramaSuvidhaApp : Application() {

    companion object {
        private const val TAG = "GramaSuvidhaApp"
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ProjectRepository(database.projectDao(), this) }

    override fun onCreate() {
        super.onCreate()
        AppLogger.i(TAG, "========================================")
        AppLogger.i(TAG, "  Grama-Suvidha Portal v1.0 Starting  ")
        AppLogger.i(TAG, "========================================")
        // Apply saved locale on app start
        LocaleHelper.applyLocale(this)
        AppLogger.d(TAG, "App initialized — database and repository ready")
    }
}
