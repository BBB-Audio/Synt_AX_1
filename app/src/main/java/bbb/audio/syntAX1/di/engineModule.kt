package bbb.audio.syntAX1.di

import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for managing the Pure Data (Pd) audio engine.
 *
 * IMPORTANT: PdEngineManager is a SINGLETON.
 * It's created once and reused everywhere.
 *
 * Dependency Graph:
 * - PdEngineManager (singleton) ← created first
 * - SynthRepository ← depends on PdEngineManager
 * - SequencerRepository ← depends on PdEngineManager
 * - ViewModels ← depend on Repositories
 */
val engineModule = module {
    single {
        PdEngineManager(androidContext())
    }
}