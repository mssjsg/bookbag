package github.io.mssjsg.bookbag.data.source

import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.data.Bookmark

/**
 * Created by Sing on 27/3/2018.
 */
interface BookmarksDataSource {
    fun getBookmarks() : LiveData<List<Bookmark>>

    fun saveBookmark(bookmark: Bookmark)

    fun updateBookmark(bookmark: Bookmark)

    fun deleteBookmarks(bookmarkUrls: List<String>)
}