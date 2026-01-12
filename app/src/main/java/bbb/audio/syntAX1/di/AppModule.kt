package bbb.audio.syntAX1.di

import bbb.audio.syntAX1.data.engine.puredata.PdEngineManager
import bbb.audio.syntAX1.data.local.AppDatabase
import bbb.audio.syntAX1.data.remote.FreesoundApiService
import bbb.audio.syntAX1.data.repository.FreesoundRepository
import bbb.audio.syntAX1.data.repository.PatchRepository
import bbb.audio.syntAX1.data.repository.PatternRepository
import bbb.audio.syntAX1.data.repository.SampleRepository
import bbb.audio.syntAX1.data.repository.SampleRepositoryInterface
import bbb.audio.syntAX1.data.repository.SampleScrubberRepository
import bbb.audio.syntAX1.data.repository.SamplerRepository
import bbb.audio.syntAX1.data.repository.SequencerRepository
import bbb.audio.syntAX1.data.repository.SettingsRepository
import bbb.audio.syntAX1.data.repository.SynthRepository
import bbb.audio.syntAX1.domain.DeleteSampleUseCase
import bbb.audio.syntAX1.domain.DownloadSoundUseCase
import bbb.audio.syntAX1.domain.GetSamplesUseCase
import bbb.audio.syntAX1.domain.LoadSampleUseCase
import bbb.audio.syntAX1.domain.PlaySamplePreviewUseCase
import bbb.audio.syntAX1.domain.SearchSoundsUseCase
import bbb.audio.syntAX1.domain.ShareSampleUseCase
import bbb.audio.syntAX1.domain.ValidatePreviewUrlUseCase
import bbb.audio.syntAX1.ui.component.audio.MediaPlayerController
import bbb.audio.syntAX1.ui.viewmodel.FreesoundViewModel
import bbb.audio.syntAX1.ui.viewmodel.LibraryViewModel
import bbb.audio.syntAX1.ui.viewmodel.SampleScrubberViewModel
import bbb.audio.syntAX1.ui.viewmodel.SamplerViewModel
import bbb.audio.syntAX1.ui.viewmodel.SequencerViewModel
import bbb.audio.syntAX1.ui.viewmodel.SettingsViewModel
import bbb.audio.syntAX1.ui.viewmodel.SynthViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // ============ NETWORK ============
    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }
    single { Gson() }
    single {
        Retrofit.Builder()
            .baseUrl("https://freesound.org/apiv2/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .build()
    }
    single { get<Retrofit>().create(FreesoundApiService::class.java) }

    // ============ DATABASE ============
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().patchDao() }
    single { get<AppDatabase>().sampleDao() }
    single { get<AppDatabase>().patternDao() }

    // ============ CONTROLLERS & HELPERS ============
    factory { MediaPlayerController(get(), get()) }

    // ============ REPOSITORIES ============
    single { PatchRepository(get()) }
    single<SampleRepositoryInterface> { SampleRepository(get()) }
    single { SampleRepository(get()) }
    single { FreesoundRepository(get(), get()) }
    single { SampleScrubberRepository() }
    single { PatternRepository(get(), get()) }
    single { SynthRepository(get<PdEngineManager>()) }
    single { SamplerRepository() }
    single { SequencerRepository(get<PdEngineManager>(), get()) }

    // ============ USECASES ============
    single { SearchSoundsUseCase(get()) }
    single { ValidatePreviewUrlUseCase(get()) }
    single { DownloadSoundUseCase(get(), get()) }
    single { GetSamplesUseCase(get<SampleRepositoryInterface>()) }
    single { DeleteSampleUseCase(get<SampleRepositoryInterface>()) }
    single { LoadSampleUseCase(get<SamplerRepository>(), androidContext()) }
    factory { PlaySamplePreviewUseCase(androidContext()) }
    single { ShareSampleUseCase() }

    // ============ VIEWMODELS ============
    viewModel { SequencerViewModel(get(), get()) }
    viewModel { FreesoundViewModel(get(), get(), get()) }
    viewModel { SynthViewModel(synthRepository = get()) }
    viewModel { LibraryViewModel(get(), get(), get(), get()) }
    viewModel { SampleScrubberViewModel(get()) }
    viewModel {
        SamplerViewModel(
            samplerRepository = get<SamplerRepository>(),
            sampleRepository = get<SampleRepository>(),
            loadSampleUseCase = get<LoadSampleUseCase>(),
            pdEngineManager = get<PdEngineManager>()
        )
    }

    // ============ SETTINGS ============
    single { get<AppDatabase>().settingsDao() }
    single { SettingsRepository(get()) }
    viewModel { SettingsViewModel(get(), get<PdEngineManager>()) }
}
