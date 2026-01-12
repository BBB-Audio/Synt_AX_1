package bbb.audio.syntAX1.di

import android.app.Application
import android.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule, engineModule)
        }
        Log.i("App", "✓ Koin initialized with engineModule + appModule")
    }

}
