package github.io.mssjsg.bookbag.data.source

import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.qualifier.LocalDataSource
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
class FoldersRepository(val localDataSource: FoldersDataSource): FoldersDataSource {
    override fun getFolders(folderId: String?): Flowable<List<Folder>> {
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
