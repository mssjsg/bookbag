package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class BookmarksLocalDataSource @Inject constructor(val bookmarksDao: BookmarksDao) : BookbagDataSource<Bookmark> {
    override fun getItem(id: String): Flowable<Bookmark> {
        return bookmarksDao.getBookmark(id)
    }

    fun updateBookmarkPreview(bookmarkUrl: String, imageUrl: String, title: String): Single<Bookmark> {
        return bookmarksDao.getBookmark(bookmarkUrl)
                .firstOrError().map { bookmark ->
                    bookmarksDao.updateBookmark(bookmark.copy(imageUrl = imageUrl, name = title, dirty = true))
                    bookmark
                }
    }

    override fun getDirtyItems(): Flowable<List<Bookmark>> {
        return bookmarksDao.getDirtyBookmarks()
    }

    override fun moveItem(url: String, folderId: String?): Single<Int> {
        return bookmarksDao.getBookmark(url)
                .firstOrError()
                .map { bookmark ->
                    bookmarksDao.updateBookmark(bookmark.copy(folderId = folderId, dirty = true))
                }
    }

    override fun getItems(folderId: String?): Flowable<List<Bookmark>> {
        return folderId?.let { bookmarksDao.getBookmarksByFolderId(it) }
                ?: bookmarksDao.getHomeBookmarks()
    }

    override fun deleteItems(bookmarkUrls: List<String>): Single<Int> {
        return Single.fromCallable({
            bookmarkUrls.onEach { url ->
                bookmarksDao.deleteBookmarkByUrl(url)
            }.size
        })
    }

    override fun saveItem(bookmark: Bookmark): Single<String> {
        return Single.fromCallable({
            bookmarksDao.insertBookmark(bookmark)
            bookmark.url
        })
    }

    override fun updateItem(bookmark: Bookmark): Single<String> {
        return Single.fromCallable({
            bookmarksDao.updateBookmark(bookmark)
            bookmark.url
        })
    }
}