package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding
import github.io.mssjsg.bookbag.main.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus

/**
 * Created by Sing on 26/3/2018.
 */
class BookmarkListItemViewHolder(private val liveBus: LiveBus, private val itemBookmarkBinding: ItemBookmarkBinding) : RecyclerView.ViewHolder(itemBookmarkBinding.root) {

    var listItem: BookmarkListItem? = null
        set(value) {
            field = value
            itemBookmarkBinding.item = listItem
            itemBookmarkBinding.executePendingBindings()
        }

    init {
        itemBookmarkBinding.root.setOnClickListener { liveBus.post(ItemClickEvent(adapterPosition)) }
        itemBookmarkBinding.root.setOnLongClickListener { liveBus.post(ItemLongClickEvent(adapterPosition)); true }
    }
}