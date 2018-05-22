package github.io.mssjsg.bookbag.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import github.io.mssjsg.bookbag.data.source.local.BookBagDatabase
import github.io.mssjsg.bookbag.data.source.local.BookmarksDao
import github.io.mssjsg.bookbag.data.source.local.FoldersDao
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
                BookBagDatabase::class.java, "bookbag.db")
                .build()
    }

    @Provides
    @Singleton
    fun provideBookmarksDao(bookBagDatabase: BookBagDatabase): BookmarksDao {
        return bookBagDatabase.bookmarksDao()
    }

    @Provides
    @Singleton
    fun provideFoldersDao(bookBagDatabase: BookBagDatabase): FoldersDao {
        return bookBagDatabase.foldersDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
}