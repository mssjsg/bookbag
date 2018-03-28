package github.io.mssjsg.bookbag.main

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import github.io.mssjsg.bookbag.data.Bookmark

/**
 * Created by Sing on 26/3/2018.
 */
object MainListBindings {
    @JvmStatic
    @BindingAdapter("items")
    fun setItems(view: RecyclerView, items: List<Bookmark>) {
        view.adapter.notifyDataSetChanged()
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(view: ImageView, url: String) {
        //TODO
    }

    @JvmStatic
    @BindingAdapter("selectedMap")
    fun setSelectedMap(view: RecyclerView, map: Map<String, Boolean>) {
        view.adapter.notifyDataSetChanged()
    }
}