package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderBinding
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus

/**
 * Created by Sing on 26/3/2018.
 */
class FolderListItemViewHolder(private val localLive: LocalLiveBus, private val itemFolderBinding: ItemFolderBinding) : RecyclerView.ViewHolder(itemFolderBinding.root) {

    var listItem: FolderListItem? = null
        set(value) {
            field = value
            itemFolderBinding.item = listItem
            itemFolderBinding.executePendingBindings()
        }

    init {
        itemFolderBinding.root.setOnClickListener { localLive.post(ItemClickEvent(adapterPosition)) }
        itemFolderBinding.root.setOnLongClickListener { localLive.post(ItemLongClickEvent(adapterPosition)); true }
    }
}