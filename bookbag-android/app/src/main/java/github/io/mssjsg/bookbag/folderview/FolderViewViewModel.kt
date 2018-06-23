package github.io.mssjsg.bookbag.folderview

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import androidx.core.util.arraySetOf
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.user.GoogleAuthHelper
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import javax.inject.Inject

class FolderViewViewModel @Inject constructor(logger: Logger,
                                              rxTransformers: RxTransformers,
                                              loadPreviewInteractor: LoadPreviewInteractor,
                                              loadListItemsInteractor: LoadListItemsInteractor,
                                              loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                              getFolderInteractor: GetFolderInteractor,
                                              val deleteFolderInteractor: DeleteFolderInteractor,
                                              val addBookmarkInteractor: AddBookmarkInteractor,
                                              val addFolderInteractor: AddFolderInteractor,
                                              val moveItemsInteractor: MoveItemsInteractor,
                                              val googleAuthHelper: GoogleAuthHelper,
                                              val bookbagUserData: BookbagUserData) : ItemListViewModel(
        logger, rxTransformers, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, getFolderInteractor) {
    lateinit var folderViewComponent: FolderViewComponent

    var pageState: ObservableField<PageState> = ObservableField(PageState.BROWSE)
    var webPageViewer: WebPageViewer? = null

    var isInMultiSelectionMode: ObservableBoolean = ObservableBoolean(false)
        private set

    private var selectedItemsCache: MutableSet<ListItem> = arraySetOf()
        private set

    init {
        isInMultiSelectionMode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!isInMultiSelectionMode.get()) {
                    for (i in items.indices) {
                        items.get(i).let {
                            if (it.isSelected) {
                                it.isSelected = false
                                items.set(i, it)
                            }
                        }
                    }
                }
            }
        })
    }


    override fun onViewLoaded(folder: String?) {
        super.onViewLoaded(folder)
        isInMultiSelectionMode.set(false)
        pageState.set(PageState.BROWSE)
    }

    override fun onItemClick(position: Int): Boolean {
        if (isInMultiSelectionMode.get()) {
            toggleSelected(position)
            if (selectedItemCount == 0) {
                isInMultiSelectionMode.set(false)
            }
            return true
        } else {
            if (super.onItemClick(position)) {
                return true
            }

            getListItem(position).let {
                when (it) {
                    is BookmarkListItem -> {
                        webPageViewer?.showPage(it.url)
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
        }
    }

    fun onSelectionModeDismissed() {
        isInMultiSelectionMode.set(false)
    }

    override fun onItemLongClick(position: Int): Boolean {
        super.onItemLongClick(position)
        isInMultiSelectionMode.set(true)
        return true
    }

    fun onFolderSelected(confirmed: Boolean, folderId: String?) {
        if (confirmed) {
            moveSelectedItems(folderId)
            loadFolder(folderId)
        }
        clearCachedSelectedItems()
    }

    fun cacheSelectedItems() {
        selectedItemsCache.clear()
        selectedItemsCache.addAll(items.filter { it.isSelected })
    }

    fun clearCachedSelectedItems() {
        selectedItemsCache.clear()
    }

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

        selectedItemsCache.forEach({ listItem ->
            when (listItem) {
                is BookmarkListItem -> {
                    selectedBookmarkUrls.add(listItem.url)
                }
                is FolderListItem -> {
                    selectedFolderIds.add(listItem.folderId)
                }
            }
        })

        if (selectedBookmarkUrls.size + selectedFolderIds.size == 0) {
            return
        }

        moveItemsInteractor.getSingle(MoveItemsInteractor.Param(selectedBookmarkUrls,
                selectedFolderIds, targetFolderId)).subscribe({
            logger.d(TAG, "moved items count $it")
        }, {
            logger.e(TAG, "failed to move items")
        })
    }

    fun signOut() {
        googleAuthHelper.signOut()
    }

    enum class PageState {
        BROWSE,
        ADD_FOLDER,
        MOVE_ITEMS
    }

    interface WebPageViewer {
        fun showPage(url: String)
    }
}