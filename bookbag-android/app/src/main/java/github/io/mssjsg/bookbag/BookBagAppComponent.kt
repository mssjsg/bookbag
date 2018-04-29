package github.io.mssjsg.bookbag

import dagger.Component
import github.io.mssjsg.bookbag.data.DataModule
import github.io.mssjsg.bookbag.util.UtilModule
import github.io.mssjsg.bookbag.util.executor.ExecutorModule
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import javax.inject.Singleton

/**
 * Created by sing on 2/11/17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, DataModule::class, ExecutorModule::class, UtilModule::class))
interface BookBagAppComponent {

    fun inject(bookBagApplication: BookBagApplication)

    fun viewModelComponent(): ViewModelComponent

    fun provideLiveBus(): LiveBus
}
