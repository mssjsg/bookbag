package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.source.remote.RemoteDataSource
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

open class BaseRepository<RemoteData, LocalData> @Inject constructor(val localDataSource: BookbagDataSource<LocalData>,
                                         val remoteDataSource: RemoteDataSource<RemoteData, LocalData>): BookbagDataSource<LocalData> {
    override fun getItem(id: String): Flowable<LocalData> {
        return localDataSource.getItem(id)
    }

    override fun getDirtyItems(): Flowable<List<LocalData>> {
        return localDataSource.getDirtyItems()
    }

    override fun moveItem(url: String, folderId: String?): Single<Int> {
        return localDataSource.moveItem(url, folderId)
    }

    override fun getItems(folderId: String?): Flowable<List<LocalData>> {
        return localDataSource.getItems(folderId)
    }

    override fun deleteItems(ids: List<String>): Single<Int> {
        return if (ids.size > 0) {
            Single.zip(listOf(localDataSource.deleteItems(ids),
                    remoteDataSource.deleteItems(ids)), {
                it.get(0) as Int
            })
        } else {
            Single.just(0)
        }
    }

    override fun saveItem(bookmark: LocalData): Single<String> {
        return Single.zip(arrayListOf(localDataSource.saveItem(bookmark),
                remoteDataSource.saveItem(bookmark)), {
            it.get(0) as String
        })
    }

    override fun updateItem(bookmark: LocalData): Single<String> {
        return Single.zip(arrayListOf(localDataSource.updateItem(bookmark),
                remoteDataSource.updateItem(bookmark)), {
            it.get(0) as String
        })
    }

}
