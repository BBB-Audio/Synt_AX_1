package bbb.audio.syntAX1.data.repository

import android.content.Context
import android.util.Log
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.data.local.dao.SampleDao
import bbb.audio.syntAX1.data.local.entity.Sample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

interface SampleRepositoryInterface {
    suspend fun getSample(id: Long): Sample?
    fun getAllSamples(): Flow<List<Sample>>
    suspend fun deleteSample(id: Long): Boolean
    suspend fun saveSample(sample: Sample): Long
    suspend fun downloadAndSaveSample(
        name: String,
        freesoundId: Long,
        previewUrl: String,
        duration: Float
    )
}

class SampleRepository(private val sampleDao: SampleDao) : SampleRepositoryInterface  {


    override suspend fun downloadAndSaveSample(
        name: String,
        freesoundId: Long,
        previewUrl: String,
        duration: Float
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Audio vom URL downloaden
                val audioBytes = downloadAudio(previewUrl)

                // In DB speichern
                val sample = Sample(
                    name = name,
                    freesoundId = freesoundId,
                    duration = duration,
                    audioData = audioBytes
                )
                sampleDao.insertSample(sample)
                Log.d("SampleRepository", "Saved: $name (${audioBytes.size} bytes)")
            } catch (e: Exception) {
                Log.e("SampleRepository", "Error: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun downloadAudio(url: String): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = okhttp3.Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                response.body?.bytes() ?: ByteArray(0)
            } catch (e: Exception) {
                Log.e("DownloadAudio", "Error: ${e.message}")
                ByteArray(0)
            }
        }
    }

    override suspend fun getSample(id: Long): Sample? {
        return withContext(Dispatchers.IO) {
            sampleDao.getSample(id)
        }
    }

    override fun getAllSamples(): Flow<List<Sample>> {
        return sampleDao.getAllSamples()
    }

    override suspend fun deleteSample(id: Long): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                sampleDao.deleteSampleById(id)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun saveSample(sample: Sample): Long {
        return withContext(Dispatchers.IO) {
            sampleDao.insertSample(sample)
        }
    }

    suspend fun initializeDefaultSamples(context: Context) {
        // Check if samples already exist
        val existingCount = sampleDao.getSampleCount()
        if (existingCount > 0) return

        // Load default samples from raw resources
        val defaultSamples = listOf(
            RawSample(R.raw.tr06_bd, "TR06 BD", 0L),
            RawSample(R.raw.tr06_sd, "TR06 SD", 1L),
            RawSample(R.raw.tr06_ch, "TR06 CH", 2L),
            RawSample(R.raw.tr06_lt, "TR06 LT", 3L),
            RawSample(R.raw.tr06_cy_rs, "TR06 CY RS", 4L)
        )

        defaultSamples.forEach { rawSample ->
            loadRawSampleToDatabase(context, rawSample)
        }
    }

    private suspend fun loadRawSampleToDatabase(context: Context, rawSample: RawSample) {
        val inputStream = context.resources.openRawResource(rawSample.resId)
        val audioBytes = inputStream.readBytes()
        inputStream.close()

        val sample = Sample(
            name = rawSample.name,
            freesoundId = rawSample.freesoundId,
            duration = 0f, // Unknown for raw resources
            audioData = audioBytes,
            fileUri = "", // Not needed for raw resources
            timestamp = System.currentTimeMillis()
        )

        sampleDao.insertSample(sample)
    }

    private data class RawSample(
        val resId: Int,
        val name: String,
        val freesoundId: Long
    )
}



