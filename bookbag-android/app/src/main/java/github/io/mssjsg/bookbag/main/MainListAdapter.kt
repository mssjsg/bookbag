package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding
import github.io.mssjsg.bookbag.databinding.ItemFolderBinding
import github.io.mssjsg.bookbag.main.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.main.listitem.FolderListItem

/**
 * Created by Sing on 26/3/2018.
 */
class MainListAdapter(val mainViewModel: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            MainViewModel.ITEM_VIEW_TYPE_FOLDER -> FolderListItemViewHolder(mainViewModel.liveBus, ItemFolderBinding.inflate(layoutInflater, parent, false))
            else -> BookmarkListItemViewHolder(mainViewModel.liveBus, ItemBookmarkBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = mainViewModel.getListItem(position)
        when(holder) {
            is BookmarkListItemViewHolder -> holder.listItem = listItem as BookmarkListItem
            is FolderListItemViewHolder -> holder.listItem = listItem as FolderListItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mainViewModel.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return mainViewModel.items.size
    }

}
