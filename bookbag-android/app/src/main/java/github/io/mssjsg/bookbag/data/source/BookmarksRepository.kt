package github.io.mssjsg.bookbag.data.source

import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.qualifier.LocalDataSource
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
class BookmarksRepository(val localDataSource: BookmarksDataSource): BookmarksDataSource {
    override fun moveBookmark(url: String, folderId: Int?) {
        localDataSource.moveBookmark(url, folderId)
    }

    override fun getBookmarks(folderId: Int?): Flowable<List<Bookmark>> {
        return localDataSource.getBookmarks(folderId)
    }

    override fun deleteBookmarks(bookmarkUrls: List<String>) {
        localDataSource.deleteBookmarks(bookmarkUrls)
    }

    override fun saveBookmark(bookmark: Bookmark) {
        localDataSource.saveBookmark(bookmark)
    }

    override fun updateBookmark(bookmark: Bookmark) {
        localDataSource.updateBookmark(bookmark)
    }

}
