package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class BookmarksLocalDataSource @Inject constructor(val bookmarksDao: BookmarksDao) : BookbagDataSource<Bookmark> {
    override fun deleteAllItems(): Completable {
        return Completable.fromCallable({
            bookmarksDao.deleteAll()
        })
    }

    override fun getItem(id: String): Single<Bookmark> {
        return bookmarksDao.getBookmark(id)
    }

    override fun getDirtyItems(): Flowable<List<Bookmark>> {
        return bookmarksDao.getDirtyBookmarks()
    }

    override fun moveItem(id: String, folderId: String?, createdDate: Long): Single<Int> {
        return bookmarksDao.getBookmark(id)
                .map { bookmark ->
                    bookmarksDao.updateBookmark(bookmark.copy(folderId = folderId, dirty = true,
                            createdDate = createdDate))
                }
    }

    override fun getItems(folderId: String?): Flowable<List<Bookmark>> {
        return folderId?.let { bookmarksDao.getBookmarksByFolderId(it) }
                ?: bookmarksDao.getHomeBookmarks()
    }

    override fun deleteItems(ids: List<String>): Single<Int> {
        return Single.fromCallable({
            ids.onEach { url ->
                bookmarksDao.deleteBookmarkByUrl(url)
            }.size
        })
    }

    override fun saveItem(item: Bookmark): Single<String> {
        return Single.fromCallable({
            bookmarksDao.insertBookmark(item)
            item.url
        })
    }

    override fun updateItem(item: Bookmark): Single<String> {
        return Single.fromCallable({
            bookmarksDao.updateBookmark(item)
            item.url
        })
    }
}