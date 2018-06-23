package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import github.io.mssjsg.bookbag.ViewModelScope
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.interactor.itemlist.GetFolderInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadFolderPathsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadListItemsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadPreviewInteractor
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
open class ItemListViewModel @Inject constructor(val logger: Logger,
                                                 val rxTransformers: RxTransformers,
                                                 val loadPreviewInteractor: LoadPreviewInteractor,
                                                 val loadListItemsInteractor: LoadListItemsInteractor,
                                                 val loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                                 val getFolderInteractor: GetFolderInteractor) : ViewModel() {

    val items: ObservableList<ListItem> = ObservableArrayList()
    val paths: ObservableList<FolderPathItem> = ObservableArrayList()
    lateinit var filteredFolders: Array<String>

    val parentFolderId: String?
        get() = currentFolder?.parentFolderId

    var selectedItemCount: Int = 0
        private set
        get() = items.filter { it.isSelected }.size

    var folderViewer: FolderViewer? = null

    var currentFolderId: String? = null
        protected set
    private lateinit var disposables: CompositeDisposable
    private var currentFolder: Folder? = null

    open fun onViewLoaded(folder: String?) {
        loadFolder(folder)
    }

    open fun onItemClick(position: Int): Boolean {
        items.get(position).let { item ->
            if (item is FolderListItem) {
                folderViewer?.showFolder(item.folderId, TransitionType.FORWARD)
                return true
            }
        }

        return false
    }

    open fun onItemLongClick(position: Int): Boolean {
        toggleSelected(position)
        return true
    }

    fun onPathSelected(folderId: String?) {
        folderId?.let {
            if (!it.isEmpty()) {
                folderViewer?.showFolder(folderId, TransitionType.BACKWARD)
            } else {
                folderViewer?.showFolder(null, TransitionType.BACKWARD)
            }
        }
    }

    open fun onBackPressed(): Boolean {
        if (currentFolderId == null) {
            return false
        }
        folderViewer?.showFolder(parentFolderId, TransitionType.BACKWARD)
        return true
    }

    protected fun loadCurrentFolder() {
        items.clear()
        paths.clear()

        if (::disposables.isInitialized) {
            disposables.dispose()
        }

        disposables = CompositeDisposable()

        disposables.add(getFolderInteractor.getFlowable(currentFolderId).subscribe({
            currentFolder = it.folder
        }))

        disposables.add(loadListItemsInteractor.getFlowable(LoadListItemsInteractor.Param(currentFolderId, filteredFolders))
                .compose(rxTransformers.applySchedulersOnFlowable()).subscribe { listItems ->
                    items.clear()
                    items.addAll(listItems)
                })

        disposables.add(loadFoldersPathsInteractor.getSingle(currentFolderId)
                .compose(rxTransformers.applySchedulersOnSingle()).subscribe { folderPathItems ->
                    paths.clear()
                    paths.addAll(folderPathItems)
                })
    }

    fun getListItem(position: Int): ListItem? {
        return items.run {
            if (size > position) get(position) else null
        }
    }

    fun loadPreview(position: Int) {
        items.get(position)?.let { item ->
            if (item is BookmarkListItem) {
                disposables.add(loadPreviewInteractor.getSingle(item.url)
                        .compose(rxTransformers.applySchedulersOnSingle())
                        .subscribe({ bookmark ->
                            logger.d(TAG, "set image preview on: ${item.url}")
                            for (i in items.indices) {
                                val currentItem = items[i]
                                if (currentItem is BookmarkListItem && currentItem.url == item.url) {
                                    var newListItem = currentItem.copy(imageUrl = bookmark.imageUrl,
                                            name = bookmark.name)
                                    items.set(i, newListItem)
                                    break
                                }
                            }
                        }, {
                            logger.e(TAG, "failed to set image preview on ${item.url}")
                        }))
            }
        }
    }

    fun setSelected(position: Int, selected: Boolean) {
        getListItem(position)?.apply {
            this.isSelected = selected
            items.set(position, this)
        }
    }

    protected fun toggleSelected(position: Int) {
        getListItem(position)?.apply {
            isSelected = !isSelected
            items.set(position, this)
        }
    }

    fun getItemViewType(position: Int): Int {
        return getListItem(position)?.let {
            when (it) {
                is BookmarkListItem -> ITEM_VIEW_TYPE_BOOKMARK
                is FolderListItem -> ITEM_VIEW_TYPE_FOLDER
                else -> ITEM_VIEW_TYPE_UNKNOWN
            }
        } ?: ITEM_VIEW_TYPE_UNKNOWN
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    protected fun loadParentFolder() {
        currentFolder?.let { loadFolder(it.parentFolderId) }
    }

    protected fun loadFolder(folderId: String?) {
        currentFolderId = folderId
        loadCurrentFolder()
    }

    fun getSelectedFolderIds(): List<String> {
        val selectedFolderIds = ArrayList<String>()
        for (listItem in items) {
            if (listItem.isSelected) {
                when (listItem) {
                    is FolderListItem -> {
                        selectedFolderIds.add(listItem.folderId)
                    }
                }
            }
        }
        return selectedFolderIds
    }

    companion object {
        const val TAG = "ItemListViewModel"
        const val ITEM_VIEW_TYPE_UNKNOWN = -1
        const val ITEM_VIEW_TYPE_BOOKMARK = 0
        const val ITEM_VIEW_TYPE_FOLDER = 1
    }

    interface FolderViewer {
        fun showFolder(folderId: String?, transitionType: TransitionType)
    }

    enum class TransitionType {
        FRESH, FORWARD, BACKWARD
    }
}
