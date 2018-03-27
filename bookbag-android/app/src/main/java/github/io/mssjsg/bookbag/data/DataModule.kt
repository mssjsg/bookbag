package github.io.mssjsg.bookbag.data

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import github.io.mssjsg.bookbag.data.qualifier.LocalDataSource
import github.io.mssjsg.bookbag.data.source.BookmarksDataSource
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.local.BookBagDatabase
import github.io.mssjsg.bookbag.data.source.local.BookmarksDao
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.util.executor.qualifier.DiskIoExecutor
import java.util.concurrent.Executor
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideBookBagDatabase(context: Context): BookBagDatabase {
        return Room.databaseBuilder(context,
                BookBagDatabase::class.java, "bookbag.db").build()
    }

    @Provides
    @Singleton
    fun provideBookmarksDao(bookBagDatabase: BookBagDatabase): BookmarksDao {
        return bookBagDatabase.bookmarksDao()
    }

    @Provides
    @Singleton
    @LocalDataSource
    fun provideLocalDataSource(@DiskIoExecutor executor: Executor,
                               bookmarksDao: BookmarksDao): BookmarksDataSource {
        return BookmarksLocalDataSource(executor, bookmarksDao)
    }

    @Provides
    @Singleton
    fun provideBookmarksRepository(@LocalDataSource dataSource: BookmarksDataSource): BookmarksRepository {
        return BookmarksRepository(dataSource)
    }
}