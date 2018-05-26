package github.io.mssjsg.bookbag.list.listitem

import github.io.mssjsg.bookbag.data.Bookmark

data class BookmarkListItem(val url: String,
                            val imageUrl: String?,
                            override val name: String,
                            override val parentFolderId: String?,
                            override var isSelected: Boolean = false,
                            override var isFiltered: Boolean = false): ListItem {

    val title: String
        get() = if(name.isEmpty()) url else name

    companion object {
        fun createItem(bookmark: Bookmark): BookmarkListItem {
            return BookmarkListItem(url = bookmark.url,
                    imageUrl = bookmark.imageUrl,
                    name = bookmark.name,
                    parentFolderId = bookmark.folderId)
        }
    }
}