package github.io.mssjsg.bookbag.data.source.remote.data

import com.google.firebase.database.IgnoreExtraProperties
import github.io.mssjsg.bookbag.data.Folder

@IgnoreExtraProperties
data class FirebaseFolder(
        var folderId: Int? = null,
        var name: String = "",
        var parentFolderId: Int? = null
) {
    companion object {
        fun create(folder: Folder): FirebaseFolder {
            return FirebaseFolder(
                    folderId = folder.folderId, name = folder.name,
                    parentFolderId = folder.parentFolderId
            )
        }
    }
}
