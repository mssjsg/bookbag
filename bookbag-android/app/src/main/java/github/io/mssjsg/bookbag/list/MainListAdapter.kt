package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding
import github.io.mssjsg.bookbag.databinding.ItemFolderBinding
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem

/**
 * Created by Sing on 26/3/2018.
 */
class MainListAdapter(val itemListViewModel: ItemListViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ItemListViewModel.ITEM_VIEW_TYPE_FOLDER -> FolderListItemViewHolder(itemListViewModel.localLiveBus, ItemFolderBinding.inflate(layoutInflater, parent, false))
            else -> BookmarkListItemViewHolder(itemListViewModel.localLiveBus, ItemBookmarkBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = itemListViewModel.getListItem(position)
        when(holder) {
            is BookmarkListItemViewHolder -> {
                holder.listItem = listItem as BookmarkListItem
                if (listItem.name.isEmpty()) {
                    itemListViewModel.loadPreview(position)
                }
            }
            is FolderListItemViewHolder -> holder.listItem = listItem as FolderListItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemListViewModel.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return itemListViewModel.items.size
    }

}
