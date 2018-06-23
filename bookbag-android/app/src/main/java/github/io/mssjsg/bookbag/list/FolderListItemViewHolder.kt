package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.databinding.ItemFolderBinding
import github.io.mssjsg.bookbag.list.listitem.FolderListItem

/**
 * Created by Sing on 26/3/2018.
 */
class FolderListItemViewHolder(private val listItemListViewModel: ItemListViewModel, private val itemFolderBinding: ItemFolderBinding) : RecyclerView.ViewHolder(itemFolderBinding.root) {

    var listItem: FolderListItem? = null
        set(value) {
            field = value
            itemFolderBinding.item = listItem
            itemFolderBinding.executePendingBindings()
        }

    init {
        itemFolderBinding.root.setOnClickListener {
            if (listItem?.isFiltered == false) {
                listItemListViewModel.onItemClick(adapterPosition)
            }
        }
        itemFolderBinding.root.setOnLongClickListener {
            listItemListViewModel.onItemLongClick(adapterPosition)
            true
        }
    }
}