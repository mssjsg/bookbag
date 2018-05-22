package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.util.BookbagSchedulers
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class BookmarksLocalDataSource @Inject constructor(val schedulers: BookbagSchedulers,
                                                   val bookmarksDao: BookmarksDao) : BookbagDataSource<Bookmark> {
    override fun getItem(id: String): Flowable<Bookmark> {
        return bookmarksDao.getBookmark(id)
    }

    fun updateBookmarkPreview(bookmarkUrl: String, imageUrl: String, title: String) {
        bookmarksDao.getBookmark(bookmarkUrl)
                .firstOrError()
                .subscribeOn(schedulers.io())
                .subscribe({ bookmark ->
                    bookmarksDao.updateBookmark(bookmark.copy(imageUrl = imageUrl, name = title, dirty = true))
                }, {})
    }

    override fun getDirtyItems(): Flowable<List<Bookmark>> {
        return bookmarksDao.getDirtyBookmarks()
    }

    override fun moveItem(url: String, folderId: String?) {
        bookmarksDao.getBookmark(url)
                .firstOrError()
                .subscribeOn(schedulers.io())
                .subscribe({ bookmark ->
                    bookmarksDao.updateBookmark(bookmark.copy(folderId = folderId, dirty = true))
                }, {})
    }

    override fun getItems(folderId: String?): Flowable<List<Bookmark>> {
        return folderId?.let { bookmarksDao.getBookmarksByFolderId(it) }
                ?: bookmarksDao.getHomeBookmarks()
    }

    override fun deleteItems(bookmarkUrls: List<String>) {
        Observable.fromCallable({
            for (url in bookmarkUrls) {
                bookmarksDao.deleteBookmarkByUrl(url)
            }
        }).subscribeOn(schedulers.io()).subscribe()
    }

    override fun saveItem(bookmark: Bookmark) {
        Observable.fromCallable({
            bookmarksDao.insertBookmark(bookmark)
        }).subscribeOn(schedulers.io()).subscribe()
    }

    override fun updateItem(bookmark: Bookmark) {
        Observable.fromCallable({
            bookmarksDao.updateBookmark(bookmark)
        }).subscribeOn(schedulers.io()).subscribe()
    }
}