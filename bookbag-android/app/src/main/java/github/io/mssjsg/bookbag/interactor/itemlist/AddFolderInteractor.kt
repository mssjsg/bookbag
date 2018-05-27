package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import io.reactivex.Single
import javax.inject.Inject

class AddFolderInteractor @Inject constructor(val uidGenerator: ItemUidGenerator,
                                              val foldersRepository: FoldersRepository): RxSingleInteractor<AddFolderInteractor.Param, String> {
    override fun getSingle(param: Param): Single<String> {
        val folderName = param.folderName
        return Single.fromCallable({
            uidGenerator.generateItemUid(folderName)
        }).flatMap({ folderId ->
            foldersRepository.saveItem(Folder(folderId = folderId,
                    name = folderName, parentFolderId = param.parentFolderId))
        })
    }

    class Param(val folderName: String, val parentFolderId: String?)
}