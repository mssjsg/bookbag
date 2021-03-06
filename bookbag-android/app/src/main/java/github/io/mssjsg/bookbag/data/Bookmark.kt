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
        val folderId:String? = null,
        val name:String = "",
        @ColumnInfo(name = "image_url")
        val imageUrl:String? = null,
        @ColumnInfo(name = "create_date")
        val createdDate:Long = System.currentTimeMillis(),
        val dirty: Boolean = true)
