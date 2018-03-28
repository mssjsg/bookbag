package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.databinding.*
import android.support.annotation.DrawableRes
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope
import javax.inject.Inject

/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
class MainViewModel @Inject constructor(val bookmarksRepository: BookmarksRepository) : ViewModel() {

    val items: ObservableList<Bookmark> = ObservableArrayList()
    val selectedMap: ObservableMap<String, Boolean> = ObservableArrayMap()

    private val itemsLiveData: LiveData<List<Bookmark>> = bookmarksRepository.getBookmarks()

    fun observe(lifecycleOwner: LifecycleOwner) {
        itemsLiveData.observe(lifecycleOwner, Observer { list ->
            items.clear()
            list?.let {
                items.addAll(list)
            }
        })
    }

    fun setSelected(url: String, selected: Boolean) {
        selectedMap.put(url, selected)
    }

    fun toggleSelected(url: String) {
        setSelected(url, !isSelected(url))
    }

    fun isSelected(url: String): Boolean {
        return selectedMap.get(url)?:false
    }

    fun isSelected(position: Int): Boolean {
        val bookmark = items.get(position)
        bookmark?.let {
            return (selectedMap.get(it.url)?: false)
        }

        return false
    }

    fun addBookmark(bookmark: Bookmark) {
        bookmarksRepository.saveBookmark(bookmark)
    }

    @DrawableRes
    fun getBackground(position: Int): Int {
        return if (isSelected(position)) R.drawable.background_selected else 0
    }
}
