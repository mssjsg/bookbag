package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding

/**
 * Created by Sing on 26/3/2018.
 */
class MainListAdapter(val mainViewModel: MainViewModel) : RecyclerView.Adapter<MainListItemViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MainListItemViewHolder(mainViewModel.liveBus, ItemBookmarkBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MainListItemViewHolder, position: Int) {
        holder.bookmark = mainViewModel.getBookmark(position)
        holder.setBackground(mainViewModel.getBackground(position))
    }

    override fun getItemCount(): Int {
        return mainViewModel.items.size
    }

}
