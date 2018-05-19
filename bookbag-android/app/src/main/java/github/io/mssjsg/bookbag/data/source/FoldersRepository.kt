package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.local.FoldersLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BaseRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.FoldersRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseFolder
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersRepository @Inject constructor(val localDataSource: FoldersLocalDataSource,
                                            val remoteDataSource: FoldersRemoteDataSource): FoldersDataSource {

    init {
        remoteDataSource.listeners.add(object: BaseRemoteDataSource.OnRemoteDataChangedListener<FirebaseFolder> {
            override fun onItemAdded(data: FirebaseFolder) {
                saveFolder(data.toDbFolder())
            }

            override fun onItemRemoved(data: FirebaseFolder) {
                deleteFolders(listOf(data.folderId))
            }

            override fun onItemUpdated(data: FirebaseFolder) {
                updateFolder(data.toDbFolder())
            }
        })
    }

    fun synchronizeToRemote() {
        localDataSource.getDirtyFolders().first(emptyList()).subscribe({ folders ->
            synchronizeToRemote(folders)
        })
    }

    fun synchronizeToRemote(folders: List<Folder>) {
        for(folder in folders) {
            remoteDataSource.saveFolder(folder)
        }
    }

    override fun getDirtyFolders(): Flowable<List<Folder>> {
        return localDataSource.getDirtyFolders()
    }

    override fun moveFolder(folderId: String, parentFolderId: String?) {
        localDataSource.moveFolder(folderId, parentFolderId)
    }

    override fun getCurrentFolder(folderId: String): Flowable<Folder> {
        return localDataSource.getCurrentFolder(folderId)
    }

    override fun getFolders(folderId: String?): Flowable<List<Folder>> {
        return localDataSource.getFolders(folderId)
    }

    override fun saveFolder(folder: Folder) {
        localDataSource.saveFolder(folder)
    }

    override fun updateFolder(folder: Folder) {
        localDataSource.updateFolder(folder)
    }

    override fun deleteFolders(folderIds: List<String>) {
        localDataSource.deleteFolders(folderIds)
        remoteDataSource.deleteFolders(folderIds)
    }
}
