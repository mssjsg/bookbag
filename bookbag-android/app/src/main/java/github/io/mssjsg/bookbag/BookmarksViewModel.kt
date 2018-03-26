package github.io.mssjsg.bookbag

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableList

import github.io.mssjsg.bookbag.data.BookmarkItem

/**
 * Created by Sing on 26/3/2018.
 */

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {

    val items: ObservableList<BookmarkItem> = ObservableArrayList<BookmarkItem>()
}
