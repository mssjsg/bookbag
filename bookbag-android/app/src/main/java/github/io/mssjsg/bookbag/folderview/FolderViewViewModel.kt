package github.io.mssjsg.bookbag.folderview

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.user.GoogleAuthHelper
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.linkpreview.SearchUrls
import javax.inject.Inject

class FolderViewViewModel @Inject constructor(logger: Logger,
                                              rxTransformers: RxTransformers,
                                              loadPreviewInteractor: LoadPreviewInteractor,
                                              loadListItemsInteractor: LoadListItemsInteractor,
                                              loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                              getFolderInteractor: GetFolderInteractor,
                                              val deleteItemsInteractor: DeleteItemsInteractor,
                                              val addBookmarkInteractor: AddBookmarkInteractor,
                                              val addFolderInteractor: AddFolderInteractor,
                                              val moveItemsInteractor: MoveItemsInteractor,
                                              val getBookmarkInteractor: GetBookmarkInteractor,
                                              val clearDatabaseInteractor: ClearDatabaseInteractor,
                                              val googleAuthHelper: GoogleAuthHelper,
                                              private val bookbagUserData: BookbagUserData) : ItemListViewModel(
        logger, rxTransformers, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, getFolderInteractor) {
    lateinit var folderViewComponent: FolderViewComponent

    var webPageViewer: WebPageViewer? = null

    var pageState: MutableLiveData<PageState> = MutableLiveData()
    var isInMultiSelectionMode: MutableLiveData<Boolean> = MutableLiveData()
        private set

    var isShowingExitConfirmNotice: MutableLiveData<Boolean> = MutableLiveData()

    private var pendingUrlFromClipboard: String? = null
    var isShowingPasteFromClipboardNotice: MutableLiveData<Boolean> = MutableLiveData()

    private val _signInOutText: MutableLiveData<SignInOutText> = MutableLiveData()
    val signInOutText: LiveData<SignInOutText> get() = _signInOutText

    init {
        pageState.value = PageState.BROWSE
        isInMultiSelectionMode.value = false
        pageState.observeForever({
            it?.apply {
                when (it) {
                    PageState.BROWSE, PageState.APP_FINISHED -> {
                        clearSelection()
                    }
                }
            }
        })

        bookbagUserData.observeForever({
            val isInOfflineMode = bookbagUserData.isInOfflineMode
            if (it == null && !isInOfflineMode) {
                pageState.value = PageState.VIEW_FINISHED
            }

            _signInOutText.value = if (isInOfflineMode) {
                SignInOutText.SIGN_IN
            } else {
                SignInOutText.SIGN_OUT
            }
        })
    }

    private fun clearSelection() {
        for (i in items.indices) {
            items.get(i).let {
                if (it.isSelected) {
                    it.isSelected = false
                    items.set(i, it)
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (currentFolderId != null) {
            return super.onBackPressed()
        }

        isShowingExitConfirmNotice.value?.let {
            if (it) {
                pageState.value = PageState.APP_FINISHED
                return true
            }
        }

        isShowingExitConfirmNotice.value = true
        return true
    }

    override fun onItemClick(position: Int): Boolean {
        if (isInMultiSelectionMode.value != null && isInMultiSelectionMode.value!!) {
            toggleSelected(position)
            if (selectedItemCount == 0) {
                pageState.value = PageState.BROWSE
                isInMultiSelectionMode.value = false
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
        isInMultiSelectionMode.value = false
        if (pageState.value == PageState.BROWSE) {
            clearSelection()
        }
    }

    fun onPasteClipboardNoticeDismissed() {
        isShowingPasteFromClipboardNotice.value = false
    }

    fun onNewFolderButtonClick() {
        pageState.value = PageState.ADDING_FOLDER
    }

    fun onSignInOutButtonClick() {
        if (bookbagUserData.isSignedIn) {
            pageState.value = PageState.CONFIRM_SIGN_OUT
        } else {
            pageState.value = PageState.VIEW_FINISHED
        }
    }

    fun onConfirmSignOut() {
        signOut()
    }

    fun onCancelSignOut() {
        pageState.value = PageState.BROWSE
    }

    fun onDeleteItemsButtonClick() {
        pageState.value = PageState.CONFIRM_DELETE
        isInMultiSelectionMode.value = false
    }

    fun onMoveItemsButtonClick() {
        pageState.value = PageState.MOVING_ITEMS
        isInMultiSelectionMode.value = false
    }

    fun onTextShared(text: String) {
        addBookmark(text)
    }

    override fun onItemLongClick(position: Int): Boolean {
        toggleSelected(position)
        isInMultiSelectionMode.value = true
        return true
    }

    fun onConfirmFolderSelection(folderId: String?) {
        when (pageState.value) {
            PageState.MOVING_ITEMS -> {
                moveSelectedItems(folderId)
                loadFolder(folderId)
            }
        }
        pageState.value = PageState.BROWSE
        isInMultiSelectionMode.value = false
    }

    fun onCancelFolderSelection() {
        pageState.value = PageState.BROWSE
        isInMultiSelectionMode.value = false
    }

    fun onConfirmNewFolderName(folderName: String) {
        addFolder(folderName)
        pageState.value = PageState.BROWSE
    }

    fun onConfirmAddBookmarkFromClipboard() {
        pendingUrlFromClipboard?.let { addBookmark(it) }
        pendingUrlFromClipboard = null
        isShowingPasteFromClipboardNotice.value = false
    }

    fun onConfirmDeleteItems() {
        deleteSelectedItems()
        pageState.value = PageState.BROWSE
        isInMultiSelectionMode.value = false
    }

    fun onCancelDeleteItems() {
        pageState.value = PageState.BROWSE
        isInMultiSelectionMode.value = false
    }

    fun onPasteClipboardText(text: String) {
        val urls = SearchUrls.matches(text)
        if (urls.size > 0) {
            val url = urls[0]
            if (pendingUrlFromClipboard != url) {
                disposables.add(getBookmarkInteractor.getSingle(url).subscribe({}, {
                    pendingUrlFromClipboard = urls[0]
                    isShowingPasteFromClipboardNotice.value = true
                }))
            }
        }
    }

    fun onCancelExitNotice() {
        isShowingExitConfirmNotice.value = false
    }

    fun onExitNoticeDismissed() {
        isShowingExitConfirmNotice.value = false
    }

    fun onCancelNewFolder() {
        pageState.value = PageState.BROWSE
    }

    private fun addBookmark(url: String) {
        addBookmarkInteractor.getSingle(AddBookmarkInteractor.Param(url, currentFolderId))
                .compose(rxTransformers.applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "bookmark saved: $url")
                }, {
                    logger.e(TAG, "failed to save bookmark")
                })
    }

    private fun addFolder(folderName: String) {
        addFolderInteractor.getSingle(AddFolderInteractor.Param(folderName, currentFolderId))
                .compose(rxTransformers.applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "folder saved: $folderName")
                }, {
                    logger.e(TAG, "failed to save folder")
                })
    }

    private fun deleteSelectedItems() {
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

        deleteItemsInteractor.getSingle(DeleteItemsInteractor.Param(selectedUrls, selectedFolderIds))
                .compose(rxTransformers.applySchedulersOnSingle()).subscribe({
                    logger.d(TAG, "items deleted count: $it")
                }, { throwable ->
                    logger.e(TAG, "failed to delete items", throwable)
                })
    }

    private fun moveSelectedItems(targetFolderId: String?) {
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

        if (selectedBookmarkUrls.size + selectedFolderIds.size == 0) {
            return
        }

        moveItemsInteractor.getSingle(MoveItemsInteractor.Param(selectedBookmarkUrls,
                selectedFolderIds, targetFolderId)).compose(rxTransformers.applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "moved items count $it")
                }, {
                    logger.e(TAG, "failed to move items")
                })
    }

    private fun signOut() {
        clearDatabaseInteractor.getCompletable(null)
                .compose(rxTransformers.applySchedulersOnCompletable())
                .subscribe({
                    googleAuthHelper.signOut()
                }, {
                    logger.e(TAG, "failed to clear data")
                })

    }

    enum class PageState {
        BROWSE,
        MOVING_ITEMS,
        ADDING_FOLDER,
        CONFIRM_DELETE,
        CONFIRM_SIGN_OUT,
        VIEW_FINISHED,
        APP_FINISHED
    }

    interface WebPageViewer {
        fun showPage(url: String)
    }

    enum class SignInOutText {
        SIGN_IN, SIGN_OUT
    }
}