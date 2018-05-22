package github.io.mssjsg.bookbag.data.source.remote.data

import com.google.firebase.database.IgnoreExtraProperties
import github.io.mssjsg.bookbag.data.Folder

@IgnoreExtraProperties
data class FirebaseFolder (
        var folderId: String = "",
        var name: String = "",
        var parentFolderId: String? = null
) {
    companion object {
        fun create(folder: Folder): FirebaseFolder {
            return FirebaseFolder(
                    folderId = folder.folderId, name = folder.name,
                    parentFolderId = folder.parentFolderId
            )
        }
    }

    fun toLocalData(): Folder {
        return Folder(folderId, name, parentFolderId, false)
    }
}
