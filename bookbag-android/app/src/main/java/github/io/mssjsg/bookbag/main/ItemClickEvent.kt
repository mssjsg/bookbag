package github.io.mssjsg.bookbag.main

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

/**
 * Created by Sing on 30/3/2018.
 */
data class ItemClickEvent(val position: Int): LiveEvent()