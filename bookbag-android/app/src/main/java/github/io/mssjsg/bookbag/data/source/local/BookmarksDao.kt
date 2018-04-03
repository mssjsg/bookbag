package github.io.mssjsg.bookbag.data.source.local

import android.arch.persistence.room.*
import github.io.mssjsg.bookbag.data.Bookmark
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
@Dao
interface BookmarksDao {
    @Query("SELECT * FROM bookmarks")
    fun getBookmarks(): Flowable<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folder_id = :folderId")
    fun getBookmarksByFolderId(folderId: String): Flowable<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark): Int

    @Query("DELETE FROM bookmarks WHERE url = :bookmarkUrl")
    fun deleteBookmarkByUrl(bookmarkUrl: String)
}