package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Bookmark
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
interface BookmarksDataSource {
    fun saveBookmark(bookmark: Bookmark)

    fun moveBookmark(url: String, folderId: String?)

    fun updateBookmark(bookmark: Bookmark)

    fun deleteBookmarks(bookmarkUrls: List<String>)

    fun getBookmarks(folderId: String? = null) : Flowable<List<Bookmark>>

    fun getDirtyBookmarks() : Flowable<List<Bookmark>>
}