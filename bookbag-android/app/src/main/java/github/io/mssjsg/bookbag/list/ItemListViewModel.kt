package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.ViewModelScope
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.itemlist.LoadFolderPathsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadPreviewInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadListItemsInteractor
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.BookbagSchedulers
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.linkpreview.JsoupWebPageCrawler
import github.io.mssjsg.bookbag.util.linkpreview.SearchUrls
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject


/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
open class ItemListViewModel @Inject constructor(val application: BookBagApplication,
                                                 val schedulers: BookbagSchedulers,
                                                 val logger: Logger,
                                                 val bookmarksRepository: BookmarksRepository,
                                                 val foldersRepository: FoldersRepository,
                                                 val liveBus: LiveBus,
                                                 val localLiveBus: LocalLiveBus,
                                                 val uidGenerator: ItemUidGenerator,
                                                 val bookbagUserData: BookbagUserData,
                                                 val loadPreviewInteractor: LoadPreviewInteractor,
                                                 val loadListItemsInteractor: LoadListItemsInteractor,
                                                 val loadFoldersPathsInteractor: LoadFolderPathsInteractor) : AndroidViewModel(application) {

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
    private lateinit var currentFolder: Folder
    lateinit var filteredFolders: Array<String>

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

        currentFolderId?.let {
            disposables.add(foldersRepository.getItem(it).subscribe({
                currentFolder = it
            }))
        }

        disposables.add(loadListItemsInteractor.getFlowable(LoadListItemsInteractor.Param(currentFolderId, filteredFolders))
                .compose(applySchedulersOnFlowable()).subscribe { listItems ->
            items.clear()
            items.addAll(listItems)
        })

        disposables.add(loadFoldersPathsInteractor.getSingle(currentFolderId).compose(applySchedulersOnSingle()).subscribe {
            folderPathItems ->
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
                        .compose(applySchedulersOnSingle())
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

    fun addBookmark(url: String) {
        val urls = SearchUrls.matches(url)
        if (urls.size > 0) {
            val detectedUrl = JsoupWebPageCrawler.extendedTrim(urls.get(0))
            if (detectedUrl.isNotEmpty()) {
                bookmarksRepository.saveItem(Bookmark(url = detectedUrl,
                        folderId = currentFolderId))
                        .compose(applySchedulersOnSingle())
                        .subscribe({
                            logger.d(TAG, "bookmark saved: $url")
                        }, {
                            logger.e(TAG, "failed to save bookmark")
                        })
            }
        }
    }

    fun addFolder(folderName: String) {
        val folderId = uidGenerator.generateItemUid(folderName)
        foldersRepository.saveItem(Folder(folderId = folderId,
                name = folderName, parentFolderId = currentFolderId)).compose(applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "folder saved: $folderName")
                }, {
                    logger.e(TAG, "failed to save folder")
                })
    }

    fun deleteSelectedItems() {
        val selectedUrls = ArrayList<String>()
        val selectedFolderIds = ArrayList<String>()
        for (listItem: ListItem in items) {
            if (listItem.isSelected) {
                when(listItem) {
                    is BookmarkListItem -> selectedUrls.add(listItem.url)
                    is FolderListItem -> selectedFolderIds.add(listItem.folderId)
                }
            }
        }

        deleteItemsRecursively(selectedUrls, selectedFolderIds)
                .compose(applySchedulersOnSingle()).subscribe({
                    logger.d(TAG, "items deleted count: $it")
                }, { throwable ->
                    logger.e(TAG, "failed to delete items", throwable)
                })
    }

    private fun deleteItemsRecursively(urls: List<String>, folderIds: List<String>): Single<Int> {
        return bookmarksRepository.deleteItems(urls).flatMap {
            if (folderIds.size > 0) {
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
            } else {
                Single.just(0)
            }
        }.map { folderContentSize ->
            urls.size + folderIds.size + folderContentSize
        }
    }

    fun getItemViewType(position: Int): Int {
        return getListItem(position)?.let {
            when(it) {
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
        if (::currentFolder.isInitialized) {
            loadFolder(currentFolder.parentFolderId)
        }
    }

    fun loadFolder(folderId: String?) {
        currentFolderId = folderId
        loadCurrentFolder()
    }

    fun getSelectedFolderIds(): List<String> {
        val selectedFolderIds =  ArrayList<String>()
        for (listItem in items) {
            if (listItem.isSelected) {
                when(listItem) {
                    is FolderListItem -> {
                        selectedFolderIds.add(listItem.folderId)
                    }
                }
            }
        }
        return selectedFolderIds
    }

    fun moveSelectedItems(targetFolderId: String?) {
        val selectedBookmarkUrls =  ArrayList<String>()
        val selectedFolderIds =  ArrayList<String>()
        for (listItem in items) {
            if (listItem.isSelected) {
                when(listItem) {
                    is BookmarkListItem -> {
                        selectedBookmarkUrls.add(listItem.url)
                    }
                    is FolderListItem -> {
                        selectedFolderIds.add(listItem.folderId)
                    }
                }
            }
        }

        selectedBookmarkUrls.forEach({ url ->
            bookmarksRepository.moveItem(url, targetFolderId).compose(applySchedulersOnSingle())
                .subscribe({
                    logger.d(TAG, "moved bookmark $url")
                }, {
                    logger.e(TAG, "failed to move bookmark $url")
                })
        })

        selectedFolderIds.forEach({ url ->
            foldersRepository.moveItem(url, targetFolderId).compose(applySchedulersOnSingle())
                    .subscribe({
                        logger.d(TAG, "moved folder $url")
                    }, {
                        logger.e(TAG, "failed to move folder $url")
                    })
        })
    }

    fun <T> applySchedulersOnObservable(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

    fun <T> applySchedulersOnFlowable(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

    fun <T> applySchedulersOnSingle(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

    companion object {
        const val TAG = "ItemListViewModel"
        const val ITEM_VIEW_TYPE_UNKNOWN = -1
        const val ITEM_VIEW_TYPE_BOOKMARK = 0
        const val ITEM_VIEW_TYPE_FOLDER = 1
    }
}
