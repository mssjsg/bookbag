package github.io.mssjsg.bookbag.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding

/**
 * Created by Sing on 26/3/2018.
 */
class MainListAdapter() : RecyclerView.Adapter<MainListItemViewHolder> () {

    var items : List<Bookmark> = emptyList()
        get() = ArrayList<Bookmark>(field)
        set(value) {
            field = ArrayList<Bookmark>(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MainListItemViewHolder(ItemBookmarkBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MainListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
