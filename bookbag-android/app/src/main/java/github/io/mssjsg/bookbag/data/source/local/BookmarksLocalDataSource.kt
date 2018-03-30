package github.io.mssjsg.bookbag.data.source.local

import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksDataSource
import java.util.concurrent.Executor

/**
 * Created by Sing on 27/3/2018.
 */
class BookmarksLocalDataSource(val executor: Executor, val bookmarksDao: BookmarksDao) : BookmarksDataSource {
    override fun deleteBookmarks(bookmarkUrls: List<String>) {
        executor.execute {
            for (url in bookmarkUrls) {
                bookmarksDao.deleteBookmarkByUrl(url)
            }
        }
    }

    override fun saveBookmark(bookmark: Bookmark) {
        executor.execute { bookmarksDao.insertBookmark(bookmark) }
    }

    override fun updateBookmark(bookmark: Bookmark) {
        executor.execute { bookmarksDao.updateBookmark(bookmark) }
    }

    override fun getBookmarks(): LiveData<List<Bookmark>> {
        return bookmarksDao.getBookmarks()
    }

}