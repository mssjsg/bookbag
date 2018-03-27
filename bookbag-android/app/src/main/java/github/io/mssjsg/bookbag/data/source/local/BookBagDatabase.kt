package github.io.mssjsg.bookbag.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import github.io.mssjsg.bookbag.data.Bookmark

/**
 * Created by Sing on 27/3/2018.
 */
@Database(entities = arrayOf(Bookmark::class), version = 1)
abstract class BookBagDatabase : RoomDatabase() {
    abstract fun bookmarksDao(): BookmarksDao
}
