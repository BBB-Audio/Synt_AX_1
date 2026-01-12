package bbb.audio.syntAX1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val sampleRate: Int = 44100,
    val outputVolume: Float = 0.8f,
    val quantizeMidi: Boolean = true,
    val usbMidiEnabled: Boolean = true,
    val themeName: String = "Default" // Added to store the selected theme name
)
