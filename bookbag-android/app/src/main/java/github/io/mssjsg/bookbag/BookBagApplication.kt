package github.io.mssjsg.bookbag

import android.app.Application
import android.content.Context
import github.io.mssjsg.bookbag.data.DataModule
import github.io.mssjsg.bookbag.util.executor.ExecutorModule
import android.support.multidex.MultiDex




/**
 * Created by Sing on 27/3/2018.
 */
class BookBagApplication: Application() {

    lateinit var appComponent: BookBagAppComponent
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerBookBagAppComponent.builder()
                .appModule(AppModule(this))
                .dataModule(DataModule())
                .executorModule(ExecutorModule())
                .build()

        appComponent.provideSyncDataManager().initialize()
    }
}