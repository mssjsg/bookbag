package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderPathBinding
import github.io.mssjsg.bookbag.main.listitem.FolderPathItem
import github.io.mssjsg.bookbag.util.livebus.LiveBus

class PathViewHolder(private val liveBus: LiveBus, private val itemFolderPathBinding: ItemFolderPathBinding): RecyclerView.ViewHolder(itemFolderPathBinding.root) {
    var folderPathItem: FolderPathItem? = null
        set(value) {
            field = value
            itemFolderPathBinding.item = value
            itemFolderPathBinding.executePendingBindings()
        }
}