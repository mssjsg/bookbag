package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.*
import android.databinding.ObservableArrayList
import android.databinding.ObservableArrayMap
import android.databinding.ObservableList
import android.databinding.ObservableMap
import android.support.annotation.DrawableRes
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope
import javax.inject.Inject

/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
class MainViewModel @Inject constructor(val bookmarksRepository: BookmarksRepository, val liveBus: LiveBus) : ViewModel() {

    var isInActionMode = false
        set(value) {
            field = value
            if (!value) selectedMap.clear()
        }

    val items: ObservableList<Bookmark> = ObservableArrayList()
    val selectedMap: ObservableMap<String, Boolean> = ObservableArrayMap()

    private val bookmarksData = bookmarksRepository.getBookmarks()

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        bookmarksData.observe(lifecycleOwner, object: Observer<List<Bookmark>> {
            override fun onChanged(t: List<Bookmark>?) {
                items.clear()
                t?.apply { items.addAll(t) }
            }
        })
    }

    private fun setSelectedByUrl(url: String, selected: Boolean) {
        selectedMap.put(url, selected)
    }

    fun getBookmark(position: Int): Bookmark? {
        return items.run {
            if (size > position) get(position) else null
        }
    }

    fun setSelected(position: Int, selected: Boolean) {
        getBookmark(position)?.apply { setSelectedByUrl(url, selected) }
    }

    fun toggleSelected(position: Int) {
        getBookmark(position)?.apply { setSelectedByUrl(url, !isSelected(url)) }
    }

    fun isSelected(url: String): Boolean {
        return selectedMap.get(url) ?: false
    }

    fun isSelected(position: Int): Boolean {
        return getBookmark(position)?.run { return isSelected(url) } ?: false
    }

    fun addBookmark(bookmark: Bookmark) {
        bookmarksRepository.saveBookmark(bookmark)
    }

    fun deleteSelectedItems() {
        val selectedUrls = ArrayList<String>()
        selectedUrls.addAll(selectedMap.filterKeys { selectedMap.get(it) ?: false }.keys)
        bookmarksRepository.deleteBookmarks(selectedUrls)
    }

    @DrawableRes
    fun getBackground(position: Int): Int {
        return if (isSelected(position)) R.drawable.background_selected else 0
    }
}
