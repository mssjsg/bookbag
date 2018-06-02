package github.io.mssjsg.bookbag.folderselection.event

import github.io.mssjsg.bookbag.util.livebus.LiveEvent

data class FolderSelectionEvent(val requestId: Int, val confirmed: Boolean, val folderId: String? = null): LiveEvent()