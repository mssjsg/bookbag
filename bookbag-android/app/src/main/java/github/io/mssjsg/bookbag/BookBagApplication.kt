package github.io.mssjsg.bookbag

import android.app.Application
import github.io.mssjsg.bookbag.data.DataModule
import github.io.mssjsg.bookbag.util.UtilModule
import github.io.mssjsg.bookbag.util.executor.ExecutorModule

/**
 * Created by Sing on 27/3/2018.
 */
class BookBagApplication: Application() {

    lateinit var appComponent: BookBagAppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerBookBagAppComponent.builder()
                .appModule(AppModule(this))
                .dataModule(DataModule())
                .executorModule(ExecutorModule())
                .utilModule(UtilModule())
                .build()
    }
}