package github.io.mssjsg.bookbag.list.listitem

class FolderListItem(name: String,
                          val folderId: String,
                          parentFolderId: String?): ListItem(name = name, parentFolderId = parentFolderId)
