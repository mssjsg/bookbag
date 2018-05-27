package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import io.reactivex.Single
import javax.inject.Inject

class MoveItemsInteractor @Inject constructor(val bookmarksRepository: BookmarksRepository,
                                              val foldersRepository: FoldersRepository): RxSingleInteractor<MoveItemsInteractor.Param, Int> {
    override fun getSingle(param: Param): Single<Int> {
        val parentFolderId = param.parentFolderId
        val sources = arrayListOf<Single<Int>>()
        sources.addAll(param.bookmarkUrls.map { url ->
            bookmarksRepository.moveItem(url, parentFolderId)
        })
        sources.addAll(param.folderIds.map { folderId ->
            foldersRepository.moveItem(folderId, parentFolderId)
        })

        return Single.zip(sources, {
            it.size
        })
    }

    class Param(val bookmarkUrls: List<String>, val folderIds: List<String>, val parentFolderId: String?)
}