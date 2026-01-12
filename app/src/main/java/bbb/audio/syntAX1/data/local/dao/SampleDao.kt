package bbb.audio.syntAX1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import bbb.audio.syntAX1.data.local.entity.Sample
import kotlinx.coroutines.flow.Flow

@Dao
interface SampleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSample(sample: Sample): Long

    @Update
    suspend fun updateSample(sample: Sample)

    @Delete
    suspend fun deleteSample(sample: Sample)

    @Query("SELECT * FROM samples WHERE id = :id")
    suspend fun getSample(id: Long): Sample?

    @Query("SELECT * FROM samples ORDER BY timestamp DESC")
    fun getAllSamples(): Flow<List<Sample>>

    @Query("SELECT * FROM samples WHERE name LIKE '%' || :searchName || '%'")
    fun searchSamples(searchName: String): Flow<List<Sample>>

    @Query("DELETE FROM samples WHERE id = :id")
    suspend fun deleteSampleById(id: Long)

    @Query("SELECT COUNT(*) FROM samples")
    suspend fun getSampleCount(): Int
}