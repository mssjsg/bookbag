package github.io.mssjsg.bookbag.main.listitem

class FolderListItem(name: String,
                          val folderId: Int,
                          parentFolderId: Int?): ListItem(name = name, parentFolderId = parentFolderId)
