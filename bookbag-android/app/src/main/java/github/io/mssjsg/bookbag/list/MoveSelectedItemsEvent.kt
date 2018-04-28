package github.io.mssjsg.bookbag.list

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

data class MoveSelectedItemsEvent(val folderId: Int?): LiveEvent()