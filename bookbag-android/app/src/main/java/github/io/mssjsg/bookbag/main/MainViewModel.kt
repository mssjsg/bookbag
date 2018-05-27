package github.io.mssjsg.bookbag.main

import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        logger: Logger,
                                        rxTransformers: RxTransformers,
                                        liveBus: LiveBus,
                                        localLiveBus: LocalLiveBus,
                                        loadPreviewInteractor: LoadPreviewInteractor,
                                        loadListItemsInteractor: LoadListItemsInteractor,
                                        loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                        getFolderInteractor: GetFolderInteractor,
                                        val deleteFolderInteractor: DeleteFolderInteractor,
                                        val addBookmarkInteractor: AddBookmarkInteractor,
                                        val addFolderInteractor: AddFolderInteractor,
                                        val moveItemsInteractor: MoveItemsInteractor) : ItemListViewModel(
        application, logger, rxTransformers, liveBus, localLiveBus, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, getFolderInteractor) {
    lateinit var mainComponent: MainComponent
    
    fun addBookmark(url: String) {
        addBookmarkInteractor.getSingle(AddBookmarkInteractor.Param(url, currentFolderId))
                .compose(rxTransformers.applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "bookmark saved: $url")
                }, {
                    logger.e(TAG, "failed to save bookmark")
                })
    }

    fun addFolder(folderName: String) {
        addFolderInteractor.getSingle(AddFolderInteractor.Param(folderName, currentFolderId))
                .compose(rxTransformers.applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "folder saved: $folderName")
                }, {
                    logger.e(TAG, "failed to save folder")
                })
    }

    fun deleteSelectedItems() {
        val selectedUrls = ArrayList<String>()
        val selectedFolderIds = ArrayList<String>()

        items.forEach({ listItem ->
            if (listItem.isSelected) {
                when (listItem) {
                    is BookmarkListItem -> selectedUrls.add(listItem.url)
                    is FolderListItem -> selectedFolderIds.add(listItem.folderId)
                }
            }
        })

        deleteFolderInteractor.getSingle(DeleteFolderInteractor.Param(selectedUrls, selectedFolderIds))
                .compose(rxTransformers.applySchedulersOnSingle()).subscribe({
                    logger.d(TAG, "items deleted count: $it")
                }, { throwable ->
                    logger.e(TAG, "failed to delete items", throwable)
                })
    }

    fun moveSelectedItems(targetFolderId: String?) {
        val selectedBookmarkUrls = ArrayList<String>()
        val selectedFolderIds = ArrayList<String>()

        items.filter { it.isSelected }.forEach({ listItem ->
            when (listItem) {
                is BookmarkListItem -> {
                    selectedBookmarkUrls.add(listItem.url)
                }
                is FolderListItem -> {
                    selectedFolderIds.add(listItem.folderId)
                }
            }
        })

        moveItemsInteractor.getSingle(MoveItemsInteractor.Param(selectedBookmarkUrls,
                selectedFolderIds, targetFolderId)).subscribe({
            logger.d(TAG, "moved items count $it")
        }, {
            logger.e(TAG, "failed to move items")
        })
    }

}