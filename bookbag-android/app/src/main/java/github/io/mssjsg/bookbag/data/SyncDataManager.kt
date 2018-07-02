package github.io.mssjsg.bookbag.data

import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.local.FoldersLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.FoldersRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.RemoteDataSource
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxSchedulers
import github.io.mssjsg.bookbag.util.RxTransformers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncDataManager @Inject constructor(val logger: Logger,
                                          val rxTransformers: RxTransformers,
                                          val userData: BookbagUserData,
                                          val schedulers: RxSchedulers,
                                          foldersLocalDataSource: FoldersLocalDataSource,
                                          bookmarksLocalDataSource: BookmarksLocalDataSource,
                                          foldersRemoteDataSource: FoldersRemoteDataSource,
                                          bookmarksRemoteDataSource: BookmarksRemoteDataSource) {

    private val bookmarkSourceSet = DataSourceSet(logger, rxTransformers,
            schedulers, bookmarksLocalDataSource, bookmarksRemoteDataSource)
    private val folderSourceSet = DataSourceSet(logger, rxTransformers,
            schedulers, foldersLocalDataSource, foldersRemoteDataSource)

    fun initialize() {
        //sync local to remote
        userData.observeForever({
            synchronizeToRemote()
        })

        //sync remote to local
        bookmarkSourceSet.listenRemoteChanges()
        folderSourceSet.listenRemoteChanges()
    }

    fun synchronizeToRemote() {
        userData.value?.let {
            bookmarkSourceSet.synchronizeToRemote()
            folderSourceSet.synchronizeToRemote()
        }
    }

    private class DataSourceSet<RemoteData, LocalData>(val logger: Logger,
                                                       val rxTransformers: RxTransformers,
                                                       val schedulers: RxSchedulers,
                                                       val localDataSource: BookbagDataSource<LocalData>,
                                                       val remoteDataSource: RemoteDataSource<RemoteData, LocalData>) {
        fun synchronizeToRemote() {
            localDataSource.getDirtyItems()
                    .distinctUntilChanged()
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.mainThread())
                    .subscribe({ bookmarks ->
                for (bookmark in bookmarks) {
                    remoteDataSource.saveItem(bookmark).subscribe({}, {
                        logger.e(TAG, "failed to sync item to remote", it)
                    })
                }
            })
        }

        fun listenRemoteChanges() {
            remoteDataSource.listeners.add(object: RemoteDataSource.OnRemoteDataChangedListener<RemoteData> {
                override fun onItemAdded(data: RemoteData) {
                    localDataSource.getItem(remoteDataSource.getIdFromRemoteData(data))
                            .subscribeOn(schedulers.io())
                            .subscribe({
                                logger.d(TAG, "item exists already")
                            }, {
                                localDataSource.saveItem(remoteDataSource.convertRemoteToLocalData(data))
                                        .compose(rxTransformers.applySchedulersOnSingle())
                                        .subscribe({}, {
                                            logger.e(TAG, "failed to save item", it)
                                })
                            })
                }

                override fun onItemRemoved(data: RemoteData) {
                    localDataSource.deleteItems(listOf(remoteDataSource.getIdFromRemoteData(data)))
                            .compose(rxTransformers.applySchedulersOnSingle())
                            .subscribe({}, {
                        logger.e(TAG, "failed to delete item", it)
                    })
                }

                override fun onItemUpdated(data: RemoteData) {
                    localDataSource.updateItem(remoteDataSource.convertRemoteToLocalData(data))
                            .compose(rxTransformers.applySchedulersOnSingle())
                            .subscribe({}, {
                        logger.e(TAG, "failed to update item", it)
                    })
                }
            })
        }
    }

    companion object {
        const val TAG = "SyncDataManager"
    }
}