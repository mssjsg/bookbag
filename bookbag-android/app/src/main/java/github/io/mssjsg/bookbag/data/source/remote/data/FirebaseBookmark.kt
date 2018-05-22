package github.io.mssjsg.bookbag.data.source.remote.data

import com.google.firebase.database.IgnoreExtraProperties
import github.io.mssjsg.bookbag.data.Bookmark

@IgnoreExtraProperties
data class FirebaseBookmark(var url:String = "",
                            var folderId:String? = null,
                            var name:String = "",
                            var createdDate:Long = 0) {
    fun toLocalData(): Bookmark {
        return Bookmark(url, folderId, name, null, createdDate, false)
    }

    companion object {
        fun create(bookmark: Bookmark): FirebaseBookmark {
            return FirebaseBookmark(url = bookmark.url, folderId = bookmark.folderId,
                    name = bookmark.name, createdDate = bookmark.createdDate);
        }
    }
}