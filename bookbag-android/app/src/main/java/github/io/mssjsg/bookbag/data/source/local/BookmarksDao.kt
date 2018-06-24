package github.io.mssjsg.bookbag.data.source.local

import android.arch.persistence.room.*
import github.io.mssjsg.bookbag.data.Bookmark
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Sing on 27/3/2018.
 */
@Dao
interface BookmarksDao {

    @Query("SELECT * FROM bookmarks WHERE url = :url")
    fun getBookmark(url: String): Single<Bookmark>

    @Query("SELECT * FROM bookmarks WHERE folder_id IS NULL")
    fun getHomeBookmarks(): Flowable<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folder_id = :folderId ORDER BY create_date DESC")
    fun getBookmarksByFolderId(folderId: String): Flowable<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark)

    @Update
    fun updateBookmark(bookmark: Bookmark): Int

    @Query("DELETE FROM bookmarks WHERE url = :bookmarkUrl")
    fun deleteBookmarkByUrl(bookmarkUrl: String)

    @Query("SELECT * FROM bookmarks WHERE dirty IS 1")
    fun getDirtyBookmarks(): Flowable<List<Bookmark>>
}