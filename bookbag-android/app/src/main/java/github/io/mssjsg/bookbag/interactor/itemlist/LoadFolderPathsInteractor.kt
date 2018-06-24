package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.util.StringProvider
import io.reactivex.Single
import javax.inject.Inject

class LoadFolderPathsInteractor @Inject constructor(val foldersRepository: FoldersRepository,
                                                    val stringProvider: StringProvider): RxSingleInteractor<String?, List<FolderPathItem>> {
    override fun getSingle(param: String?): Single<List<FolderPathItem>> {
        return getFolders(param, param).map {
            it.map {
                FolderPathItem(it.name, it.folderId)
            }
        }
    }

    private fun getFolders(currentFolderId: String?, folderId: String?, folderPathItems: MutableList<Folder> = ArrayList()): Single<List<Folder>> {
        folderId?.let {
            return foldersRepository.getItem(it).flatMap {
                folderPathItems.add(0, it)
                getFolders(currentFolderId, it.parentFolderId, folderPathItems)
            }
        } ?:let {
            folderPathItems.add(0, Folder("", stringProvider.getString(R.string.path_home)))
            return Single.just(folderPathItems)
        }
    }
}