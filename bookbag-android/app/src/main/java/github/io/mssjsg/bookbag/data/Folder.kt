package github.io.mssjsg.bookbag.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "folders")
data class Folder(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "folder_id")
        val folderId: Int? = null,
        val name: String,
        @ColumnInfo(name = "parent_folder_id")
        val parentFolderId: Int? = null,
        val dirty: Boolean = true
)
