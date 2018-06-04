package github.io.mssjsg.bookbag

import dagger.Component
import github.io.mssjsg.bookbag.data.DataModule
import github.io.mssjsg.bookbag.data.SyncDataManager
import github.io.mssjsg.bookbag.folderselection.FolderSelectionComponent
import github.io.mssjsg.bookbag.folderview.FolderViewComponent
import github.io.mssjsg.bookbag.intro.IntroComponent
import github.io.mssjsg.bookbag.main.MainComponent
import github.io.mssjsg.bookbag.util.UtilModule
import github.io.mssjsg.bookbag.util.executor.ExecutorModule
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import javax.inject.Singleton

/**
 * Created by sing on 2/11/17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, DataModule::class, ExecutorModule::class,
        UtilModule::class))
interface BookBagAppComponent {

    fun inject(bookBagApplication: BookBagApplication)

    fun folderSelectionComponent(): FolderSelectionComponent

    fun folderViewComponent(): FolderViewComponent

    fun mainComponent(): MainComponent

    fun introComponent(): IntroComponent

    fun provideLiveBus(): LiveBus
    fun provideSyncDataManager(): SyncDataManager
}
