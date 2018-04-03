package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderBinding
import github.io.mssjsg.bookbag.main.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus

/**
 * Created by Sing on 26/3/2018.
 */
class FolderListItemViewHolder(private val liveBus: LiveBus, private val itemFolderBinding: ItemFolderBinding) : RecyclerView.ViewHolder(itemFolderBinding.root) {

    var listItem: FolderListItem? = null
        set(value) {
            field = value
            itemFolderBinding.item = listItem
            itemFolderBinding.executePendingBindings()
        }

    init {
        itemFolderBinding.root.setOnClickListener { liveBus.post(ItemClickEvent(adapterPosition)) }
        itemFolderBinding.root.setOnLongClickListener { liveBus.post(ItemLongClickEvent(adapterPosition)); true }
    }
}