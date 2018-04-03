package github.io.mssjsg.bookbag.data.source.local

import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksDataSource
import github.io.mssjsg.bookbag.data.source.FoldersDataSource
import io.reactivex.Flowable
import java.util.concurrent.Executor

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersLocalDataSource(val executor: Executor, val foldersDao: FoldersDao) : FoldersDataSource {
    override fun getFolders(folderId: String?): Flowable<List<Folder>> {
        return folderId?.let { foldersDao.getFoldersByParentFolderId(folderId) }
                ?: foldersDao.getFolders()
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