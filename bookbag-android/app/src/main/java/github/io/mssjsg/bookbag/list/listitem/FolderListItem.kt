package github.io.mssjsg.bookbag.list.listitem

class FolderListItem(name: String,
                          val folderId: Int,
                          parentFolderId: Int?): ListItem(name = name, parentFolderId = parentFolderId)
