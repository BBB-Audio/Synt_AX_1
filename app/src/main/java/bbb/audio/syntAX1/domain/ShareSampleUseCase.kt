package bbb.audio.syntAX1.domain

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import bbb.audio.syntAX1.data.engine.WavUtils
import bbb.audio.syntAX1.data.local.entity.Sample
import java.io.File
import java.io.FileOutputStream

/**
 * Use case for sharing a sample.
 * It now intelligently creates a valid WAV file by either returning the data
 * if it already has a header, or adding one if it's missing raw PCM data.
 */
class ShareSampleUseCase {

    /**
     * Executes the share action.
     * @param context The Android context, required for file operations and intents.
     * @param sample The sample object containing the raw audio data.
     * @return A Result object indicating success or failure.
     */
    operator fun invoke(context: Context, sample: Sample): Result<Unit> {
        return try {
            // 1. INTELLIGENTLY create a complete WAV file in memory.
            // This now checks for an existing header before adding a new one.
            val wavBytes = WavUtils.createWavBytes(sample.audioData)

            // 2. Sanitize the sample name to create a safe filename.
            val safeFilename = sample.name.replace(Regex("[^a-zA-Z0-9.-]"), "_") + ".wav"

            // 3. Create a temporary file in the app's cache directory.
            val tempFile = File(context.cacheDir, safeFilename)

            // 4. Write the WAV data to the temporary file.
            FileOutputStream(tempFile).use { os ->
                os.write(wavBytes)
            }

            // 5. Get a secure content URI for the temporary file.
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, tempFile)

            // 6. Create and launch the standard Android share intent.
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "audio/wav"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share Sample '${sample.name}'")
            context.startActivity(chooserIntent)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(Exception("Could not share sample: ${e.message}", e))
        }
    }
}