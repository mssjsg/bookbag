package github.io.mssjsg.bookbag.list

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

/**
 * Created by Sing on 30/3/2018.
 */
data class ItemLongClickEvent(val position: Int): LiveEvent()