package github.io.mssjsg.bookbag.list.event

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

data class PathClickEvent(val folderId: String?): LiveEvent()