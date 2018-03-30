package github.io.mssjsg.bookbag.main

import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.ItemBookmarkBinding
import github.io.mssjsg.bookbag.util.livebus.LiveBus

/**
 * Created by Sing on 26/3/2018.
 */
class MainListItemViewHolder(private val liveBus: LiveBus, private val itemBookmarkBinding: ItemBookmarkBinding) : RecyclerView.ViewHolder(itemBookmarkBinding.root) {

    var bookmark: Bookmark? = null
        set(value) {
            field = value
            itemBookmarkBinding.item = bookmark
            itemBookmarkBinding.executePendingBindings()
        }

    init {
        itemBookmarkBinding.root.setOnClickListener { liveBus.post(ItemClickEvent(adapterPosition)) }
        itemBookmarkBinding.root.setOnLongClickListener { liveBus.post(ItemLongClickEvent(adapterPosition)); true }
    }

    fun setBackground(@DrawableRes backgroundResId: Int) {
        if (backgroundResId != 0) {
            itemBookmarkBinding.background = ResourcesCompat.getDrawable(itemView.resources, backgroundResId, null)
        } else {
            itemBookmarkBinding.background = null
        }
    }
}