package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.source.remote.RemoteDataSource
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

open class BaseRepository<RemoteData, LocalData> @Inject constructor(
        private val localDataSource: BookbagDataSource<LocalData>,
        private val remoteDataSource: RemoteDataSource<RemoteData, LocalData>
): BookbagDataSource<LocalData> {

    override fun getItem(id: String): Single<LocalData> {
        return localDataSource.getItem(id)
    }

    override fun getDirtyItems(): Flowable<List<LocalData>> {
        return localDataSource.getDirtyItems()
    }

    override fun moveItem(id: String, folderId: String?): Single<Int> {
        return Single.zip(listOf(localDataSource.moveItem(id, folderId),
                remoteDataSource.moveItem(id, folderId)), {
            it[0] as Int
        })
    }

    override fun getItems(folderId: String?): Flowable<List<LocalData>> {
        return localDataSource.getItems(folderId)
    }

    override fun deleteItems(ids: List<String>): Single<Int> {
        return if (ids.isNotEmpty()) {
            Single.zip(listOf(localDataSource.deleteItems(ids),
                    remoteDataSource.deleteItems(ids)), {
                it[0] as Int
            })
        } else {
            Single.just(0)
        }
    }

    override fun saveItem(item: LocalData): Single<String> {
        return Single.zip(arrayListOf(localDataSource.saveItem(item),
                remoteDataSource.saveItem(item)), {
            it[0] as String
        })
    }

    override fun updateItem(item: LocalData): Single<String> {
        return Single.zip(arrayListOf(localDataSource.updateItem(item),
                remoteDataSource.updateItem(item)), {
            it[0] as String
        })
    }

}
