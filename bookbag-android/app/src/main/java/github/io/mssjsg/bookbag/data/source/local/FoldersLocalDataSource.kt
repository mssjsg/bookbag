package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersDataSource
import io.reactivex.Flowable
import java.util.concurrent.Executor

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersLocalDataSource(val executor: Executor, val foldersDao: FoldersDao) : FoldersDataSource {
    override fun moveFolder(folderId: Int, parentFolderId: Int?) {
        executor.execute {
            foldersDao.moveFolder(folderId, parentFolderId)
        }
    }

    override fun getCurrentFolder(folderId: Int): Flowable<Folder> {
        return foldersDao.getCurrentFolderByFolderId(folderId)
    }

    override fun getFolders(folderId: Int?): Flowable<List<Folder>> {
        return folderId?.let {
            foldersDao.getFoldersByParentFolderId(folderId)
        } ?: foldersDao.getHomeFolders()
    }

    override fun saveFolder(folder: Folder) {
        executor.execute {
            foldersDao.insertFolder(folder)
        }
    }

    override fun updateFolder(folder: Folder) {
        executor.execute {
            foldersDao.updateFolder(folder)
        }
    }

    override fun deleteFolders(folderIds: List<Int>) {
        executor.execute {
            for (folderId in folderIds) {
                foldersDao.deleteFolderByFolderId(folderId)
            }
        }
    }
}