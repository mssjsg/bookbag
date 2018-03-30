package github.io.mssjsg.bookbag.data.source.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import github.io.mssjsg.bookbag.data.Bookmark

/**
 * Created by Sing on 27/3/2018.
 */
@Dao
interface BookmarksDao {
    @Query("SELECT * FROM bookmarks")
    fun getBookmarks(): LiveData<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark): Int

    @Query("DELETE FROM bookmarks WHERE url = :bookmarkUrl")
    fun deleteBookmarkByUrl(bookmarkUrl: String)
}