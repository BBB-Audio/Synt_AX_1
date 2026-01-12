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
package bbb.audio.syntAX1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bbb.audio.syntAX1.data.local.dao.PatchDao
import bbb.audio.syntAX1.data.local.dao.PatternDao
import bbb.audio.syntAX1.data.local.dao.SampleDao
import bbb.audio.syntAX1.data.local.dao.SettingsDao
import bbb.audio.syntAX1.data.local.entity.Pattern
import bbb.audio.syntAX1.data.local.entity.Sample
import bbb.audio.syntAX1.data.model.Patch



@Database(
    entities = [
        Patch::class,
        Sample::class,
        AppSettings::class,
        Pattern::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patchDao(): PatchDao
    abstract fun sampleDao(): SampleDao
    abstract fun settingsDao(): SettingsDao
    abstract fun patternDao(): PatternDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "syntax_database"
                )
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}