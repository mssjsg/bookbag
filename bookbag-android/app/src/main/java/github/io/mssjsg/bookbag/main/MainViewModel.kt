package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.databinding.Observable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope
import javax.inject.Inject

/**
 * Created by Sing on 26/3/2018.
 */
@ViewModelScope
class MainViewModel @Inject constructor(val bookmarksRepository: BookmarksRepository) : ViewModel() {

    val items: ObservableList<Bookmark> = ObservableArrayList<Bookmark>()

    private val itemsLiveData: LiveData<List<Bookmark>> = bookmarksRepository.getBookmarks()

    fun observe(lifecycleOwner: LifecycleOwner) {
        itemsLiveData.observe(lifecycleOwner, Observer { list ->
            items.clear()
            list?.let {
                items.addAll(list)
            }
        })
    }

    fun addBookmark(bookmark: Bookmark) {
        bookmarksRepository.saveBookmark(bookmark)
    }
}
