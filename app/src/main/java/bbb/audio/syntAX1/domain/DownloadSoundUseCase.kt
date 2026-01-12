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
package bbb.audio.syntAX1.domain

import bbb.audio.syntAX1.data.repository.FreesoundRepository
import bbb.audio.syntAX1.data.repository.SampleRepository

/**
 * Use case for downloading a sound from Freesound and saving it to the local library.
 * This encapsulates the complete business logic flow: fetching sound details,
 * downloading the audio, and persisting it to the local database.
 */
class DownloadSoundUseCase(
    private val freesoundRepository: FreesoundRepository,
    private val sampleRepository: SampleRepository
) {
    /**
     * Executes the sound download workflow.
     *
     * @param soundId The unique identifier of the sound on Freesound
     * @param apiKey Your Freesound API key for authentication
     * @return A [Result] containing the saved sound's ID on success,
     *         or an exception on failure
     */
    suspend operator fun invoke(
        soundId: Int,
        apiKey: String,
        customName: String? = null
    ): Result<Long> {
        return try {
            // 1. Fetch sound details (including the preview URL)
            val detail = freesoundRepository.getSoundDetail(soundId, apiKey)
            val previewUrl = detail.previews?.previewHqMp3
                ?: return Result.failure(IllegalStateException("Sound has no preview available"))

            val nameToSave = customName ?: detail.name

            // 2. Download audio and save to database
            sampleRepository.downloadAndSaveSample(
                name = nameToSave,
                freesoundId = soundId.toLong(),
                previewUrl = previewUrl,
                duration = detail.duration
            )

            // 3. Return success with the sound ID
            Result.success(soundId.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}