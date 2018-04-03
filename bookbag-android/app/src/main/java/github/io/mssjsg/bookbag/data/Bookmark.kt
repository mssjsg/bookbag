package github.io.mssjsg.bookbag.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Sing on 26/3/2018.
 */
@Entity(tableName = "bookmarks")
data class Bookmark(
        @PrimaryKey
        val url:String,
        @ColumnInfo(name = "folder_id")
        val folderId:Int? = null,
        val name:String = url,
        val imageUrl:String? = null)
