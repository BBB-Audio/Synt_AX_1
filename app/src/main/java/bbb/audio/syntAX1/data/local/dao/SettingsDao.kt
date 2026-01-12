package bbb.audio.syntAX1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bbb.audio.syntAX1.data.local.AppSettings

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettings(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun saveSettings(settings: AppSettings)

    @Query("UPDATE app_settings SET themeName = :themeName WHERE id = 1")
    suspend fun updateTheme(themeName: String)
}