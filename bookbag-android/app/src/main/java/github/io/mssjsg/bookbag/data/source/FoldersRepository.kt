package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.local.FoldersLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.FoldersRemoteDataSource
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersRepository @Inject constructor(val localDataSource: FoldersLocalDataSource, val remoteDataSource: FoldersRemoteDataSource): FoldersDataSource {

    init {
        localDataSource.getDirtyFolders().subscribe({
            for(folder in it) {
                remoteDataSource.saveFolder(folder)
                val cleanFolder = folder.copy(dirty = false)
                localDataSource.saveFolder(cleanFolder)
            }
        })
    }

    override fun getDirtyFolders(): Flowable<List<Folder>> {
        return localDataSource.getDirtyFolders()
    }

    override fun moveFolder(folderId: Int, parentFolderId: Int?) {
        localDataSource.moveFolder(folderId, parentFolderId)
    }

    override fun getCurrentFolder(folderId: Int): Flowable<Folder> {
        return localDataSource.getCurrentFolder(folderId)
    }

    override fun getFolders(folderId: Int?): Flowable<List<Folder>> {
        return localDataSource.getFolders(folderId)
    }

    override fun saveFolder(folder: Folder) {
        localDataSource.saveFolder(folder)
    }

    override fun updateFolder(folder: Folder) {
        localDataSource.updateFolder(folder)
    }

    override fun deleteFolders(folderIds: List<Int>) {
        localDataSource.deleteFolders(folderIds)
        remoteDataSource.deleteFolders(folderIds)
    }
}
