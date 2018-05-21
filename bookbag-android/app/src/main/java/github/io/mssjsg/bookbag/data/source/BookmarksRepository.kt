package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BaseRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by Sing on 27/3/2018.
 */
class BookmarksRepository @Inject constructor(val localDataSource: BookmarksLocalDataSource,
                          val remoteDataSource: BookmarksRemoteDataSource): BookmarksDataSource {

    init {
        remoteDataSource.listeners.add(object: BaseRemoteDataSource.OnRemoteDataChangedListener<FirebaseBookmark> {
            override fun onItemAdded(data: FirebaseBookmark) {
                saveBookmark(data.toDbBookmark())
            }

            override fun onItemRemoved(data: FirebaseBookmark) {
                deleteBookmarks(listOf(data.url))
            }

            override fun onItemUpdated(data: FirebaseBookmark) {
            }
        })
    }

    fun updateBookmarkPreview(bookmarkUrl: String, imageUrl: String, title: String) {
        localDataSource.updateBookmarkPreview(bookmarkUrl, imageUrl, title)
    }

    fun synchronizeToRemote() {
        localDataSource.getDirtyBookmarks().first(emptyList()).subscribe({ bookmarks ->
            synchronizeToRemote(bookmarks)
        })
    }

    fun synchronizeToRemote(bookmarks: List<Bookmark>) {
        for(bookmark in bookmarks) {
            remoteDataSource.saveBookmark(bookmark)
        }
    }

    override fun getDirtyBookmarks(): Flowable<List<Bookmark>> {
        return localDataSource.getDirtyBookmarks()
    }

    override fun moveBookmark(url: String, folderId: String?) {
        localDataSource.moveBookmark(url, folderId)
    }

    override fun getBookmarks(folderId: String?): Flowable<List<Bookmark>> {
        return localDataSource.getBookmarks(folderId)
    }

    override fun deleteBookmarks(bookmarkUrls: List<String>) {
        localDataSource.deleteBookmarks(bookmarkUrls)
        remoteDataSource.deleteBookmarks(bookmarkUrls)
    }

    override fun saveBookmark(bookmark: Bookmark) {
        localDataSource.saveBookmark(bookmark)
    }

    override fun updateBookmark(bookmark: Bookmark) {
        localDataSource.updateBookmark(bookmark)
    }

}
