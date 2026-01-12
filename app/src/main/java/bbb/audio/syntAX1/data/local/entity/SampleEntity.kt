package bbb.audio.syntAX1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "samples")
data class Sample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val freesoundId: Long = 0L,
    val duration: Float = 0f,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val audioData: ByteArray = ByteArray(0),
    val fileUri: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Sample
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}