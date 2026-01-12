package bbb.audio.syntAX1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import bbb.audio.syntAX1.data.model.Patch
import kotlinx.coroutines.flow.Flow

@Dao
interface PatchDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPatch(patch: Patch): Long

    @Update
    suspend fun updatePatch(patch: Patch)

    @Delete
    suspend fun deletePatch(patch: Patch)

    @Query("SELECT * FROM patches WHERE id = :id")
    suspend fun getPatch(id: Long): Patch?

    @Query("SELECT * FROM patches ORDER BY timestamp DESC")
    fun getAllPatches(): Flow<List<Patch>>

    @Query("SELECT * FROM patches WHERE name LIKE '%' || :searchName || '%'")
    fun searchPatches(searchName: String): Flow<List<Patch>>

    @Query("DELETE FROM patches WHERE id = :id")
    suspend fun deletePatchById(id: Long)
}