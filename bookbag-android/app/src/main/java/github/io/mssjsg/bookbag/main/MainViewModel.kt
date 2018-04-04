package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.main.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.main.listitem.FolderListItem
import github.io.mssjsg.bookbag.main.listitem.ListItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
class MainViewModel @Inject constructor(val bookmarksRepository: BookmarksRepository,
                                        val foldersRepository: FoldersRepository,
                                        val liveBus: LiveBus) : ViewModel() {

    companion object {
        const val ITEM_VIEW_TYPE_UNKNOWN = -1
        const val ITEM_VIEW_TYPE_BOOKMARK = 0
        const val ITEM_VIEW_TYPE_FOLDER = 1
    }

    var isInActionMode = false
        set(value) {
            field = value
            for (listItem: ListItem in items) {
                listItem.isSelected = false
            }
        }

    val items: ObservableList<ListItem> = ObservableArrayList()

    private val listItemsDisposable: Disposable

    init {
        listItemsDisposable = Flowable.combineLatest(bookmarksRepository.getBookmarks().map {
            val items: MutableList<ListItem> = ArrayList()
            for (bookmark: Bookmark in it) {
                items.add(BookmarkListItem(bookmark.name, bookmark.url, bookmark.folderId))
            }
            items
        }, foldersRepository.getFolders().map {
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ listItems: List<ListItem> ->
            items.clear()
            items.addAll(listItems)
        })
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
    }
}
