package github.io.mssjsg.bookbag.data.source.local

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class FoldersLocalDataSource @Inject constructor(val foldersDao: FoldersDao) : BookbagDataSource<Folder> {
    override fun deleteAllItems(): Completable {
        return Completable.fromCallable({
            foldersDao.deleteAll()
        })
    }

    override fun getDirtyItems(): Flowable<List<Folder>> {
        return foldersDao.getDirtyFolders()
    }

    override fun moveItem(folderId: String, parentFolderId: String?, createdDate: Long): Single<Int> {
        return foldersDao.getFolder(folderId)
                .map { folder ->
                    foldersDao.updateFolder(folder.copy(parentFolderId = parentFolderId, dirty = true))
                }
    }

    override fun getItem(folderId: String): Single<Folder> {
        return foldersDao.getFolder(folderId)
    }

    override fun getItems(folderId: String?): Flowable<List<Folder>> {
        return folderId?.let {
            foldersDao.getFoldersByParentFolderId(folderId)
        } ?: foldersDao.getHomeFolders()
    }

    override fun saveItem(folder: Folder): Single<String> {
        return Single.fromCallable({
            foldersDao.insertFolder(folder)
            folder.folderId
        })
    }

    override fun updateItem(folder: Folder): Single<String> {
        return Single.fromCallable({
            foldersDao.updateFolder(folder)
            folder.folderId
        })
    }

    override fun deleteItems(folderIds: List<String>): Single<Int> {
        return Single.fromCallable({
            folderIds.onEach { folderId ->
                foldersDao.deleteFolderByFolderId(folderId)
            }.size
        })
    }
}