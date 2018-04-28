package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderPathBinding
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus

class PathViewHolder(private val localLiveBus: LocalLiveBus, private val itemFolderPathBinding: ItemFolderPathBinding): RecyclerView.ViewHolder(itemFolderPathBinding.root) {
    var folderPathItem: FolderPathItem? = null
        set(value) {
            field = value
            itemFolderPathBinding.item = value
            itemFolderPathBinding.executePendingBindings()
        }
}