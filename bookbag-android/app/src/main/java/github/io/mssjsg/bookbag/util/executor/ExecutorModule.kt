package github.io.mssjsg.bookbag.util.executor

import dagger.Module
import dagger.Provides
import github.io.mssjsg.bookbag.util.executor.qualifier.DiskIoExecutor
import github.io.mssjsg.bookbag.util.executor.qualifier.NetworkIoExecutor
import github.io.mssjsg.bookbag.util.executor.qualifier.UiExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Module
class ExecutorModule {

    companion object {
        const val THREAD_COUNT = 3
    }

    @Provides
    @Singleton
    @UiExecutor
    fun provideUiExecutor(): Executor {
        return MainThreadExecutor()
    }

    @Provides
    @Singleton
    @NetworkIoExecutor
    fun provideNetworkIoExecutor(): Executor {
        return Executors.newFixedThreadPool(THREAD_COUNT)
    }

    @Provides
    @Singleton
    @DiskIoExecutor
    fun provideDiskIoExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }
}