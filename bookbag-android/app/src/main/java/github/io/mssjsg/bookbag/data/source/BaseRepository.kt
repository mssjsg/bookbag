package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.source.remote.RemoteDataSource
import io.reactivex.Flowable
import javax.inject.Inject

open class BaseRepository<RemoteData, LocalData> @Inject constructor(val localDataSource: BookbagDataSource<LocalData>,
                                         val remoteDataSource: RemoteDataSource<RemoteData, LocalData>): BookbagDataSource<LocalData> {
    override fun getItem(id: String): Flowable<LocalData> {
        return localDataSource.getItem(id)
    }

    override fun getDirtyItems(): Flowable<List<LocalData>> {
        return localDataSource.getDirtyItems()
    }

    override fun moveItem(url: String, folderId: String?) {
        localDataSource.moveItem(url, folderId)
    }

    override fun getItems(folderId: String?): Flowable<List<LocalData>> {
        return localDataSource.getItems(folderId)
    }

    override fun deleteItems(bookmarkUrls: List<String>) {
        localDataSource.deleteItems(bookmarkUrls)
        remoteDataSource.deleteItems(bookmarkUrls)
    }

    override fun saveItem(bookmark: LocalData) {
        localDataSource.saveItem(bookmark)
    }

    override fun updateItem(bookmark: LocalData) {
        localDataSource.updateItem(bookmark)
    }

}
