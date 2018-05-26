package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxFlowableInteractor
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class LoadListItemsInteractor @Inject constructor(val bookmarksRepository: BookmarksRepository,
                                                  val foldersRepository: FoldersRepository): RxFlowableInteractor<LoadListItemsInteractor.Param, List<ListItem>> {
    override fun getFlowable(param: Param): Flowable<List<ListItem>> {
        return Flowable.combineLatest(bookmarksRepository.getItems(param.currentFolderId).map {
            val items = arrayListOf<BookmarkListItem>()
            for (bookmark: Bookmark in it) {
                val bookmarkListItem = BookmarkListItem.createItem(bookmark)
                items.add(bookmarkListItem)
            }
            items
        }, foldersRepository.getItems(param.currentFolderId).map {
            val items = arrayListOf<FolderListItem>()
            for (folder: Folder in it) {
                val item = FolderListItem.createItem(folder)
                folder.folderId.let {
                    item.isFiltered = param.filteredFoldersIds.contains(it)
                }

                items.add(item)
            }
            items
        }, BiFunction<List<ListItem>, List<ListItem>, List<ListItem>> { bookmarks, folders ->
            val items: MutableList<ListItem> = ArrayList()
            items.addAll(folders)
            items.addAll(bookmarks)
            items
        })
    }

    class Param(val currentFolderId: String?, val filteredFoldersIds: Array<String>)
}