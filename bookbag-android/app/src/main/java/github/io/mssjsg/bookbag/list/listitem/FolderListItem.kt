package github.io.mssjsg.bookbag.list.listitem

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder

data class FolderListItem(val folderId: String,
                          override val name: String,
                          override val parentFolderId: String?,
                          override var isSelected: Boolean = false,
                          override var isFiltered: Boolean = false): ListItem {
    companion object {
        fun createItem(folder: Folder): FolderListItem {
            return FolderListItem(folderId = folder.folderId,
                    name = folder.name,
                    parentFolderId = folder.folderId)
        }
    }
}
