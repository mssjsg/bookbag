package github.io.mssjsg.bookbag.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.databinding.ItemFolderPathBinding

class PathListAdapter(private val itemListViewModel: ItemListViewModel): RecyclerView.Adapter<PathViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PathViewHolder(itemListViewModel,
                ItemFolderPathBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return itemListViewModel.paths.size
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        holder.folderPathItem = itemListViewModel.paths.get(position)
    }
}
