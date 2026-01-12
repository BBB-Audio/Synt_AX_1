package bbb.audio.syntAX1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import bbb.audio.syntAX1.data.local.entity.Pattern
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPattern(pattern: Pattern): Long

    @Update
    suspend fun updatePattern(pattern: Pattern)

    @Delete
    suspend fun deletePattern(pattern: Pattern)

    @Query("SELECT * FROM patterns WHERE id = :id")
    suspend fun getPattern(id: Long): Pattern?

    @Query("SELECT * FROM patterns ORDER BY timestamp DESC")
    fun getAllPatterns(): Flow<List<Pattern>>

    @Query("SELECT * FROM patterns WHERE name LIKE '%' || :searchName || '%'")
    fun searchPatterns(searchName: String): Flow<List<Pattern>>

    @Query("DELETE FROM patterns WHERE id = :id")
    suspend fun deletePatternById(id: Long)
}