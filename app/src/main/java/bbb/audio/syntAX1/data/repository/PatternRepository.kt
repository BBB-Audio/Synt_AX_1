package bbb.audio.syntAX1.data.repository

import android.util.Log
import bbb.audio.syntAX1.data.local.dao.PatternDao
import bbb.audio.syntAX1.data.local.entity.Pattern
import bbb.audio.syntAX1.ui.viewmodel.SequencerStepState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class PatternRepository(
    private val patternDao: PatternDao,
    private val gson: Gson  // Inject Gson from Koin
) {

    /**
     * Save current sequencer state as a pattern
     */
    suspend fun savePattern(
        name: String,
        steps: List<SequencerStepState>,
        bpm: Float,
        description: String = ""
    ): Long {
        val stepsJson = gson.toJson(steps)
        val pattern = Pattern(
            name = name,
            stepsJson = stepsJson,
            bpm = bpm,
            description = description
        )
        return patternDao.insertPattern(pattern)
    }

    /**
     * Load a pattern and restore it to SequencerUiState
     */
    suspend fun loadPattern(id: Long): Pattern? {
        return patternDao.getPattern(id)
    }

    /**
     * Parse pattern JSON back to SequencerStepState list
     */
    fun parsePatternSteps(pattern: Pattern): List<SequencerStepState> {
        return try {
            gson.fromJson(pattern.stepsJson, object : TypeToken<List<SequencerStepState>>() {}.type)
        } catch (e: Exception) {
            Log.e("PatternRepository", "Error parsing pattern steps: ${e.message}")
            emptyList()
        }
    }

    fun getAllPatterns(): Flow<List<Pattern>> = patternDao.getAllPatterns()

    suspend fun deletePattern(id: Long) = patternDao.deletePatternById(id)
}