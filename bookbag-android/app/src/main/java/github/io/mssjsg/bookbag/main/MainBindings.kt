package github.io.mssjsg.bookbag.main

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import github.io.mssjsg.bookbag.list.listitem.FolderPathItem
import github.io.mssjsg.bookbag.list.listitem.ListItem

/**
 * Created by Sing on 26/3/2018.
 */
object MainBindings {
    @JvmStatic
    @BindingAdapter("items")
    fun setItems(view: RecyclerView, items: List<ListItem>) {
        view.adapter.notifyDataSetChanged()
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(view: ImageView, url: String) {
        //TODO
    }

    @JvmStatic
    @BindingAdapter("paths")
    fun setFolderPaths(view: RecyclerView, paths: List<FolderPathItem>) {
        view.adapter.notifyDataSetChanged()
    }
}