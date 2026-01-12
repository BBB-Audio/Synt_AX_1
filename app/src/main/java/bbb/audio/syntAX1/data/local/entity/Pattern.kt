package bbb.audio.syntAX1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class Pattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val stepsJson: String,  // Serialized List<SequencerStepState> as JSON
    val bpm: Float = 120f,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
)