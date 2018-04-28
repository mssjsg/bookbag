package github.io.mssjsg.bookbag.list.listitem

class BookmarkListItem(name: String, val url: String, parentFolderId: Int?):
        ListItem(name = name, parentFolderId = parentFolderId)