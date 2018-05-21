package github.io.mssjsg.bookbag.data.source.local

import android.arch.persistence.room.*
import github.io.mssjsg.bookbag.data.Bookmark
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
@Dao
interface BookmarksDao {

    @Query("SELECT * FROM bookmarks WHERE url = :url")
    fun getBookmark(url: String): Bookmark

    @Query("SELECT * FROM bookmarks WHERE folder_id IS NULL")
    fun getHomeBookmarks(): Flowable<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folder_id = :folderId ORDER BY create_date DESC")
    fun getBookmarksByFolderId(folderId: String): Flowable<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBookmark(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark): Int

    @Query("UPDATE bookmarks SET image_url = :imageUrl AND name = :title WHERE url = :bookmarkUrl")
    fun updateBookmarkPreview(bookmarkUrl: String, imageUrl: String, title: String)

    @Query("DELETE FROM bookmarks WHERE url = :bookmarkUrl")
    fun deleteBookmarkByUrl(bookmarkUrl: String)

    @Query("UPDATE bookmarks SET folder_id = :folderId AND dirty = 1 WHERE url = :url")
    fun moveBookmark(url: String, folderId: String?)

    @Query("SELECT * FROM bookmarks WHERE dirty IS 1")
    fun getDirtyBookmarks(): Flowable<List<Bookmark>>
}