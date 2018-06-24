package github.io.mssjsg.bookbag.data

import android.util.Log
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.local.FoldersLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.FoldersRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.RemoteDataSource
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.RxSchedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncDataManager @Inject constructor(val userData: BookbagUserData,
                                          val schedulers: RxSchedulers,
                                          foldersLocalDataSource: FoldersLocalDataSource,
                                          bookmarksLocalDataSource: BookmarksLocalDataSource,
                                          foldersRemoteDataSource: FoldersRemoteDataSource,
                                          bookmarksRemoteDataSource: BookmarksRemoteDataSource) {

    private val bookmarkSourceSet = DataSourceSet(schedulers, bookmarksLocalDataSource, bookmarksRemoteDataSource)
    private val folderSourceSet = DataSourceSet(schedulers, foldersLocalDataSource, foldersRemoteDataSource)

    fun initialize() {
        //sync local to remote
        userData.observeForever({ user ->
            user?.let { user ->
                bookmarkSourceSet.synchronizeToRemote()
                folderSourceSet.synchronizeToRemote()
            }
        })

        //sync local to remote
        bookmarkSourceSet.listenRemoteChanges()
        folderSourceSet.listenRemoteChanges()
    }

    private class DataSourceSet<RemoteData, LocalData>(val schedulers: RxSchedulers, val localDataSource: BookbagDataSource<LocalData>, val remoteDataSource: RemoteDataSource<RemoteData, LocalData>) {
        fun synchronizeToRemote() {
            localDataSource.getDirtyItems()
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.mainThread())
                    .subscribe({ bookmarks ->
                for (bookmark in bookmarks) {
                    remoteDataSource.saveItem(bookmark)
                }
            })
        }

        fun listenRemoteChanges() {
            remoteDataSource.listeners.add(object: RemoteDataSource.OnRemoteDataChangedListener<RemoteData> {
                override fun onItemAdded(data: RemoteData) {
                    localDataSource.getItem(remoteDataSource.getIdFromRemoteData(data))
                            .subscribeOn(schedulers.io())
                            .subscribe({
                                if (it == null) {
                                    localDataSource.saveItem(remoteDataSource.convertRemoteToLocalData(data))
                                }
                            }, {
                                Log.e(TAG, "item not found")
                            })
                }

                override fun onItemRemoved(data: RemoteData) {
                    localDataSource.deleteItems(listOf(remoteDataSource.getIdFromRemoteData(data)))
                }

                override fun onItemUpdated(data: RemoteData) {
                    localDataSource.updateItem(remoteDataSource.convertRemoteToLocalData(data))
                }
            })
        }
    }

    companion object {
        const val TAG = "SyncDataManager"
    }
}