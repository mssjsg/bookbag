package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderPathBinding
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem

class PathViewHolder(private val itemListViewModel: ItemListViewModel, private val itemFolderPathBinding: ItemFolderPathBinding): RecyclerView.ViewHolder(itemFolderPathBinding.root) {
    var folderPathItem: FolderPathItem? = null
        set(value) {
            field = value
            itemFolderPathBinding.item = value
            itemFolderPathBinding.executePendingBindings()
        }

    init {
        itemFolderPathBinding.root.setOnClickListener { itemListViewModel.onPathSelected(folderPathItem?.folderId) }
    }
}