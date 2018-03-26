package github.io.mssjsg.bookbag

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.data.BookmarkItem
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding

/**
 * Created by Sing on 26/3/2018.
 */
class MainListItemViewHolder(private val itemBookmarkBinding: ItemBookmarkBinding) : RecyclerView.ViewHolder(itemBookmarkBinding.root) {
    fun bind(bookmarkItem: BookmarkItem) {
        itemBookmarkBinding.item = bookmarkItem
        itemBookmarkBinding.executePendingBindings()
    }
}