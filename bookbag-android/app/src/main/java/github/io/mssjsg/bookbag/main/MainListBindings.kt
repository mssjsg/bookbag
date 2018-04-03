package github.io.mssjsg.bookbag.main

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import github.io.mssjsg.bookbag.main.listitem.ListItem

/**
 * Created by Sing on 26/3/2018.
 */
object MainListBindings {
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
}