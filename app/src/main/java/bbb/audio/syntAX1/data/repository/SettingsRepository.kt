package bbb.audio.syntAX1.data.repository

import android.util.Log
import bbb.audio.syntAX1.data.local.AppSettings
import bbb.audio.syntAX1.data.local.dao.SettingsDao

/**
 * SettingsRepository
 *
 * Handles persistence of app settings to/from Room Database.
 */
class SettingsRepository(
    private val settingsDao: SettingsDao
) {
    /**
     * Update theme in database
     */
    suspend fun updateTheme(themeName: String) {
        settingsDao.updateTheme(themeName)
    }

    /**
     * Load settings from database
     * Returns default if not found
     */
    suspend fun loadSettings(): AppSettings {
        return try {
            settingsDao.getSettings() ?: AppSettings()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading settings: ${e.message}")
            AppSettings()  // Return defaults on error
        }
    }

    /**
     * Save settings to database
     */
    suspend fun saveSettings(settings: AppSettings) {
        try {
            settingsDao.saveSettings(settings)
            Log.d(TAG, "Settings saved: $settings")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving settings: ${e.message}")
            throw e
        }
    }

    companion object {
        private const val TAG = "SettingsRepository"
    }
}