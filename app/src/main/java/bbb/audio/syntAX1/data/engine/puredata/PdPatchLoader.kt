/*
 * Copyright 2026 Dirk Hammacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package bbb.audio.syntAX1.data.engine.puredata

import android.content.Context
import android.util.Log
import org.puredata.core.PdBase
import org.puredata.core.utils.IoUtils
import java.io.File

class PdPatchLoader(private val context: Context) {

   // private var dispatcher: PdUiDispatcher? = null
    private val patchesDir: File = File(context.filesDir, "patches")
    private val samplesDir: File = File(context.filesDir, "samples")

    init {
        // Create directories on initialization
        patchesDir.mkdirs()
        samplesDir.mkdirs()
    }

    fun getPatchesDirectory(): File {
        return File(context.filesDir, "patches")
    }

    /**
     * Load a patch file with all dependent resources
     * @param patchRawResId The resource ID of the .pd file (or .zip)
     * @param patchFileName The name of the patch file
     * @param dependentWavFiles List of dependent wave files from res.raw
     * @return Boolean: true if loaded successfully, false otherwise
     */
    fun loadPatch(
        patchRawResId: Int,
        patchFileName: String,
        dependentWavFiles: List<Pair<Int, String>> = emptyList()
    ): Boolean = try {
        // 1. Extract all Wave-Files first
        for ((wavResId, wavFileName) in dependentWavFiles) {
            extractWaveFile(wavResId, wavFileName)
        }

        // 2. Extract/copy the patch file
        val patchFile = extractPatchFile(patchRawResId, patchFileName)
        if (patchFile == null) {
            Log.e(TAG, "Failed to extract patch file: $patchFileName")
            false
        } else {
            // 3. load the Patch in libpd
            Log.d(TAG, "Loading patch: ${patchFile.absolutePath}")
            PdBase.openPatch(patchFile.absolutePath)
            Log.d(TAG, "Patch loaded successfully")
            true
        }
    } catch (e: Exception) {
        Log.e(TAG, "Exception loading patch: ${e.message}", e)
        false
    }

    /**
     * Extract a wave file from res.raw to filesDir/samples/
     */
    private fun extractWaveFile(wavRawResId: Int, wavFileName: String): File {
        val wavFile = File(samplesDir, wavFileName)

        if (!wavFile.exists()) {
            try {
                context.resources.openRawResource(wavRawResId).use { inputStream ->
                    wavFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d(TAG, "Extracted wave file: ${wavFile.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting wave file: ${e.message}", e)
            }
        }

        return wavFile
    }

    /**
     * Extract the patch file (can be .pd or .zip)
     */
    private fun extractPatchFile(patchRawResId: Int, patchFileName: String): File? {
        return try {
            val isZip = patchFileName.endsWith(".zip")
            val targetFileName = if (isZip) {
                patchFileName.removeSuffix(".zip") + ".pd"  // mvpsynth.pd
            } else {
                patchFileName
            }

            val targetFile = File(patchesDir, targetFileName)

            if (!targetFile.exists()) {
                context.resources.openRawResource(patchRawResId).use { inputStream ->
                    if (isZip) {
                        IoUtils.extractZipResource(inputStream, patchesDir, true)
                        Log.d(TAG, "Extracted zip patches to: ${patchesDir.absolutePath}")
                    } else {
                        targetFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        Log.d(TAG, "Copied patch file to: ${targetFile.absolutePath}")
                    }
                }
            }

            targetFile
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting patch file: ${e.message}", e)
            null
        }
    }

    /**
     * Returns the path to a sample file (for use in Pd)
     */
    fun getSamplePath(wavFileName: String): String {
        return File(samplesDir, wavFileName).absolutePath
    }

    /**
     * Returns the path to the patches directory
     */
    fun getPatchesDir(): String {
        return patchesDir.absolutePath
    }

    companion object {
        private const val TAG = "PdPatchLoader"
    }
}