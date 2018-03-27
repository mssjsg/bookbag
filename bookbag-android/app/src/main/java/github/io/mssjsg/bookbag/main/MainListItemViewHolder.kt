package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding

/**
 * Created by Sing on 26/3/2018.
 */
class MainListItemViewHolder(private val itemBookmarkBinding: ItemBookmarkBinding) : RecyclerView.ViewHolder(itemBookmarkBinding.root) {
    fun bind(bookmark: Bookmark) {
        itemBookmarkBinding.item = bookmark
        itemBookmarkBinding.executePendingBindings()
    }
}