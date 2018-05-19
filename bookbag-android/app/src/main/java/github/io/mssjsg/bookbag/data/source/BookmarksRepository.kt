package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by Sing on 27/3/2018.
 */
class BookmarksRepository @Inject constructor(val localDataSource: BookmarksLocalDataSource,
                          val remoteDataSource: BookmarksRemoteDataSource): BookmarksDataSource {

    init {
        localDataSource.getDirtyBookmarks().subscribe({
            for(folder in it) {
                remoteDataSource.saveBookmark(folder)
                val cleanFolder = folder.copy(dirty = false)
                localDataSource.saveBookmark(cleanFolder)
            }
        })
    }

    override fun getDirtyBookmarks(): Flowable<List<Bookmark>> {
        return localDataSource.getDirtyBookmarks()
    }

    override fun moveBookmark(url: String, folderId: Int?) {
        localDataSource.moveBookmark(url, folderId)
    }

    override fun getBookmarks(folderId: Int?): Flowable<List<Bookmark>> {
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
