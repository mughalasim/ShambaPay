package ke.co.shambapay

import android.app.Application
import ke.co.shambapay.di.KoinContext
import ke.co.shambapay.di.applicationModule
import ke.co.shambapay.di.useCaseModule
import ke.co.shambapay.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinContext.koinApp = startKoin {
            androidContext(this@App)
            modules(
                listOf (
                    applicationModule,
                    viewModelModule,
                    useCaseModule
                )
            )
        }
    }

}