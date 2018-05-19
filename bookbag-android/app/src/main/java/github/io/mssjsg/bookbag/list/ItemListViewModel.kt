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
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.user.BookbagUserManager
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import github.io.mssjsg.bookbag.util.linkpreview.JsoupWebPageCrawler
import github.io.mssjsg.bookbag.util.linkpreview.LinkPreviewException
import github.io.mssjsg.bookbag.util.linkpreview.SearchUrls
import github.io.mssjsg.bookbag.util.linkpreview.UrlPreviewManager
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
open class ItemListViewModel @Inject constructor(val application: BookBagApplication,
                                                 val bookmarksRepository: BookmarksRepository,
                                                 val foldersRepository: FoldersRepository,
                                                 val liveBus: LiveBus,
                                                 val localLiveBus: LocalLiveBus,
                                                 val uidGenerator: ItemUidGenerator,
                                                 val bookbagUserManager: BookbagUserManager,
                                                 val urlPreviewManager: UrlPreviewManager) : AndroidViewModel(application) {

    companion object {
        const val ITEM_VIEW_TYPE_UNKNOWN = -1
        const val ITEM_VIEW_TYPE_BOOKMARK = 0
        const val ITEM_VIEW_TYPE_FOLDER = 1
    }

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

    fun loadPreview(bookmarkListItem: BookmarkListItem) {
        disposables.add(Observable.fromCallable({
            var previewUrl = ""
            var title = bookmarkListItem.name
            try {
                val item = urlPreviewManager.get(bookmarkListItem.url)
                previewUrl = item.previewUrl
                title = item.title
            } catch (e: LinkPreviewException) { }
            bookmarksRepository.updateBookmarkPreview(bookmarkListItem.url, previewUrl, title)
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({}, {}))
    }

    fun loadCurrentFolder() {
        items.clear()
        paths.clear()

        if (::disposables.isInitialized) {
            disposables.dispose()
        }

        disposables = CompositeDisposable()

        disposables.add(Flowable.combineLatest(Flowable.just(isShowingBookmarks).flatMap {
            isShowingBookmarks ->
            if (isShowingBookmarks) {
                bookmarksRepository.getBookmarks(currentFolderId).map {
                    val items = arrayListOf<BookmarkListItem>()
                    for (bookmark: Bookmark in it) {
                        val bookmarkListItem = BookmarkListItem(bookmark.name, bookmark.url,
                                bookmark.folderId, bookmark.imageUrl)
                        items.add(bookmarkListItem)
                    }
                    items
                }
            } else {
                Flowable.just(ArrayList())
            }
        }.doOnNext({
            for (bookmarkListItem in it) {
                if(bookmarkListItem.imageUrl == null) {
                    loadPreview(bookmarkListItem)
                }
            }
        }), foldersRepository.getFolders(currentFolderId).map {
            val items = arrayListOf<FolderListItem>()
            for (folder: Folder in it) {
                val item = FolderListItem(folder.name, folderId = folder.folderId,
                        parentFolderId = folder.parentFolderId)
                folder.folderId?.let {
                    item.isFiltered = filteredFolders.contains(it)
                }

                items.add(item)
            }
            items
        }, BiFunction<List<ListItem>, List<ListItem>, List<ListItem>> { bookmarks, folders ->
            val items: MutableList<ListItem> = ArrayList()
            items.addAll(folders)
            items.addAll(bookmarks)
            items
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { listItems ->
            items.clear()
            items.addAll(listItems)
        })

        disposables.add(getFolders(currentFolderId).map {
            it.map {
                FolderPathItem(it.name, it.folderId)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            folderPathItems ->
            paths.clear()
            paths.addAll(folderPathItems)
        })
    }

    private fun getFolders(folderId: String?, folderPathItems: MutableList<Folder> = ArrayList()): Single<List<Folder>> {
        folderId?.let {
            return foldersRepository.getCurrentFolder(it).firstOrError().doAfterSuccess {
                if (it.folderId == currentFolderId) {
                    currentFolder = it
                }
            }.flatMap {
                folderPathItems.add(0, it)
                getFolders(it.parentFolderId, folderPathItems)
            }
        } ?:let {
            folderPathItems.add(0, Folder("", application.getString(R.string.path_home)))
            return Single.just(folderPathItems)
        }
    }

    fun getListItem(position: Int): ListItem? {
        return items.run {
            if (size > position) get(position) else null
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
                bookmarksRepository.saveBookmark(Bookmark(url = detectedUrl,
                        folderId = currentFolderId))
            }
        }
    }

    fun addFolder(folderName: String) {
        val folderId = uidGenerator.generateItemUid(folderName)
        foldersRepository.saveFolder(Folder(folderId = folderId,
                name = folderName, parentFolderId = currentFolderId))
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
        bookmarksRepository.deleteBookmarks(selectedUrls)
        foldersRepository.deleteFolders(selectedFolderIds)
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

        for (url in selectedBookmarkUrls) {
            bookmarksRepository.moveBookmark(url, targetFolderId)
        }

        for (folderId in selectedFolderIds) {
            foldersRepository.moveFolder(folderId, targetFolderId)
        }
    }
}
