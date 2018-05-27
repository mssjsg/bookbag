package github.io.mssjsg.bookbag.list.event

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

data class FolderSelectedEvent(val folderId: Int?): LiveEvent()
