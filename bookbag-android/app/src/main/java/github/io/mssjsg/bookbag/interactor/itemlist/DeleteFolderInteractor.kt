package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class DeleteFolderInteractor @Inject constructor(val bookmarksRepository: BookmarksRepository,
                                                 val foldersRepository: FoldersRepository): RxSingleInteractor<DeleteFolderInteractor.Param, Int> {
    override fun getSingle(param: Param): Single<Int> {
        return deleteItemsRecursively(param.urls, param.folderIds)
    }

    private fun deleteItemsRecursively(urls: List<String>, folderIds: List<String>): Single<Int> {
        return bookmarksRepository.deleteItems(urls).flatMap {
            foldersRepository.deleteItems(folderIds).flatMap {
                Single.zip(folderIds.map({ folderId ->
                    Single.zip<List<Folder>, List<Bookmark>, Pair<List<Bookmark>, List<Folder>>>(
                            foldersRepository.getItems(folderId).firstOrError(),
                            bookmarksRepository.getItems(folderId).firstOrError(),
                            object : BiFunction<List<Folder>, List<Bookmark>, Pair<List<Bookmark>, List<Folder>>> {
                                override fun apply(folderList: List<Folder>, bookmarkList: List<Bookmark>): Pair<List<Bookmark>, List<Folder>> {
                                    return Pair(bookmarkList, folderList)
                                }
                            }
                    ).flatMap { pair ->
                        deleteItemsRecursively(pair.first.map { it.url }, pair.second.map { it.folderId })
                    }
                }), {
                    it.sumBy { it as Int }
                })
            }
        }.map { folderContentSize ->
            urls.size + folderIds.size + folderContentSize
        }
    }

    class Param(val urls: List<String>, val folderIds: List<String>)
}