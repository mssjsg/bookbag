package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.databinding.ItemFolderPathBinding

class PathListAdapter(private val mainViewModel: MainViewModel): RecyclerView.Adapter<PathViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PathViewHolder(mainViewModel.liveBus,
                ItemFolderPathBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return mainViewModel.paths.size
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        holder.folderPathItem = mainViewModel.paths.get(position)
    }
}
