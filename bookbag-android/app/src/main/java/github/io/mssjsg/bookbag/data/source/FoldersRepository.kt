package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersRepository(val localDataSource: FoldersDataSource): FoldersDataSource {
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
    }
}
