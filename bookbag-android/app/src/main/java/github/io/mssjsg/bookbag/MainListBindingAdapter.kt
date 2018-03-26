package github.io.mssjsg.bookbag

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.data.BookmarkItem

/**
 * Created by Sing on 26/3/2018.
 */
object MainListBindingAdapter {
    @JvmStatic
    @BindingAdapter("items")
    fun setItems(view: RecyclerView, item: List<BookmarkItem>) {
        val adapter: MainListAdapter = view.adapter as MainListAdapter
        adapter.items = item
        adapter.notifyDataSetChanged()
    }
}