package github.io.mssjsg.bookbag.list.listitem

interface ListItem {
    val name: String
    val parentFolderId: String?
    var isSelected: Boolean
    var isFiltered: Boolean
}