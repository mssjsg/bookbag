package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus

/**
 * Created by Sing on 26/3/2018.
 */
class BookmarkListItemViewHolder(private val localLiveBus: LocalLiveBus, private val itemBookmarkBinding: ItemBookmarkBinding) : RecyclerView.ViewHolder(itemBookmarkBinding.root) {

    var listItem: BookmarkListItem? = null
        set(value) {
            field = value
            itemBookmarkBinding.item = listItem
            itemBookmarkBinding.executePendingBindings()
        }

    init {
        itemBookmarkBinding.root.setOnClickListener { localLiveBus.post(ItemClickEvent(adapterPosition)) }
        itemBookmarkBinding.root.setOnLongClickListener { localLiveBus.post(ItemLongClickEvent(adapterPosition)); true }
    }
}