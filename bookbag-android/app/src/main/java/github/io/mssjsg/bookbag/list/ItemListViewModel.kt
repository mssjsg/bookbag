package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
                                            val localLiveBus: LocalLiveBus) : AndroidViewModel(application) {

    companion object {
        const val ITEM_VIEW_TYPE_UNKNOWN = -1
        const val ITEM_VIEW_TYPE_BOOKMARK = 0
        const val ITEM_VIEW_TYPE_FOLDER = 1
    }

    var isInActionMode = false
        set(value) {
            field = value
            if (!value) {
                for (listItem: ListItem in items) {
                    listItem.isSelected = false
                }
            }
        }

    var currentFolderId: Int? = null
        private set

    val items: ObservableList<ListItem> = ObservableArrayList()

    val paths: ObservableList<FolderPathItem> = ObservableArrayList()

    var isShowingBookmarks: Boolean = true

    private lateinit var listItemsDisposable: Disposable
    private lateinit var folderPathItemsDisposable: Disposable
    private lateinit var currentFolder: Folder

    fun loadCurrentFolder() {
        items.clear()
        paths.clear()

        if (::listItemsDisposable.isInitialized) {
            listItemsDisposable.dispose()
        }

        if (::folderPathItemsDisposable.isInitialized) {
            folderPathItemsDisposable.dispose()
        }

        listItemsDisposable = Flowable.combineLatest(Flowable.just(isShowingBookmarks).flatMap {
            isShowingBookmarks ->
            if (isShowingBookmarks) {
                bookmarksRepository.getBookmarks(currentFolderId).map {
                    val items: MutableList<ListItem> = ArrayList()
                    for (bookmark: Bookmark in it) {
                        items.add(BookmarkListItem(bookmark.name, bookmark.url, bookmark.folderId))
                    }
                    items
                }
            } else {
                Flowable.just(ArrayList())
            }
        }, foldersRepository.getFolders(currentFolderId).map {
            val items: MutableList<ListItem> = ArrayList()
            for (folder: Folder in it) {
                items.add(FolderListItem(folder.name, folderId = folder.folderId ?: -1,
                        parentFolderId = folder.parentFolderId))
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
        }

        folderPathItemsDisposable = getFolders(currentFolderId).map {
            it.map {
                FolderPathItem(it.name, it.folderId)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            folderPathItems ->
            paths.clear()
            paths.addAll(folderPathItems)
        }
    }

    private fun getFolders(folderId: Int?, folderPathItems: MutableList<Folder> = ArrayList()): Single<List<Folder>> {
        folderId?.let {
            return foldersRepository.getCurrentFolder(it).firstOrError().doAfterSuccess {
                currentFolder = it
            }.flatMap {
                folderPathItems.add(0, it)
                getFolders(it.parentFolderId, folderPathItems)
            }
        } ?:let {
            folderPathItems.add(0, Folder(null, application.getString(R.string.path_home)))
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

    fun addBookmark(bookmark: Bookmark) {
        bookmarksRepository.saveBookmark(bookmark)
    }

    fun addFolder(folder: Folder) {
        foldersRepository.saveFolder(folder)
    }

    fun deleteSelectedItems() {
        val selectedUrls = ArrayList<String>()
        val selectedFolderIds = ArrayList<Int>()
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
        listItemsDisposable.dispose()
        folderPathItemsDisposable.dispose()
    }

    fun loadParentFolder() {
        loadFolder(currentFolder.parentFolderId)
    }

    fun loadFolder(folderId: Int?) {
        currentFolderId = folderId
        loadCurrentFolder()
    }

    fun moveSelectedItems(targetFolderId: Int?) {
        val selectedBookmarkUrls =  ArrayList<String>()
        val selectedFolderIds =  ArrayList<Int>()
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
