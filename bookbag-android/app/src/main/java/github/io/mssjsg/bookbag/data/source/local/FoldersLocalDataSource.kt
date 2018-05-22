package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.util.BookbagSchedulers
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class FoldersLocalDataSource @Inject constructor(val schedulers: BookbagSchedulers,
                                                 val foldersDao: FoldersDao) : BookbagDataSource<Folder> {
    override fun getDirtyItems(): Flowable<List<Folder>> {
        return foldersDao.getDirtyFolders()
    }

    override fun moveItem(folderId: String, parentFolderId: String?) {
        foldersDao.getFolder(folderId)
                .firstOrError()
                .subscribeOn(schedulers.io())
                .subscribe({ folder ->
                    foldersDao.updateFolder(folder.copy(parentFolderId = parentFolderId, dirty = true))
                }, {})
    }

    override fun getItem(folderId: String): Flowable<Folder> {
        return foldersDao.getFolder(folderId)
    }

    override fun getItems(folderId: String?): Flowable<List<Folder>> {
        return folderId?.let {
            foldersDao.getFoldersByParentFolderId(folderId)
        } ?: foldersDao.getHomeFolders()
    }

    override fun saveItem(folder: Folder) {
        Observable.fromCallable({
            foldersDao.insertFolder(folder)
        }).subscribeOn(schedulers.io()).subscribe()
    }

    override fun updateItem(folder: Folder) {
        Observable.fromCallable({
            foldersDao.updateFolder(folder)
        }).subscribeOn(schedulers.io()).subscribe()
    }

    override fun deleteItems(folderIds: List<String>) {
        Observable.fromCallable({
            for (folderId in folderIds) {
                foldersDao.deleteFolderByFolderId(folderId)
            }
        }).subscribeOn(schedulers.io()).subscribe()
    }
}