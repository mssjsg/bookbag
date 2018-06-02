package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import androidx.core.util.arraySetOf
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.ViewModelScope
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
open class ItemListViewModel @Inject constructor(val application: BookBagApplication,
                                                 val logger: Logger,
                                                 val rxTransformers: RxTransformers,
                                                 val liveBus: LiveBus,
                                                 val localLiveBus: LocalLiveBus,
                                                 val loadPreviewInteractor: LoadPreviewInteractor,
                                                 val loadListItemsInteractor: LoadListItemsInteractor,
                                                 val loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                                 val getFolderInteractor: GetFolderInteractor) : AndroidViewModel(application) {

    var isInMultiSelectionMode = false
        set(value) {
            field = value
            if (!value) {
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

    var currentFolderId: String? = null
    val items: ObservableList<ListItem> = ObservableArrayList()
    val paths: ObservableList<FolderPathItem> = ObservableArrayList()
    var isShowingBookmarks: Boolean = true

    private lateinit var disposables: CompositeDisposable
    private var currentFolder: Folder? = null
    lateinit var filteredFolders: Array<String>

    val parentFolderId: String?
        get() = currentFolder?.parentFolderId

    var selectedItemCount: Int = 0
        private set
        get() = items.filter { it.isSelected }.size

    fun loadCurrentFolder() {
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

    fun toggleSelected(position: Int) {
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

    fun loadParentFolder() {
        currentFolder?.let { loadFolder(it.parentFolderId) }
    }

    fun loadFolder(folderId: String?) {
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
}
