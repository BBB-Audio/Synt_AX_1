package bbb.audio.syntAX1.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patches")
data class Patch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val synthStateBlob: ByteArray = ByteArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Patch
        return id == other.id && name == other.name
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode() + name.hashCode()
    }
}