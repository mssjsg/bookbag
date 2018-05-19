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
                .addMigrations(object: Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE folders ADD COLUMN dirty INTEGER NOT NULL DEFAULT 1");
                        database.execSQL("ALTER TABLE bookmarks ADD COLUMN dirty INTEGER NOT NULL DEFAULT 1");
                    }

                }).build()
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