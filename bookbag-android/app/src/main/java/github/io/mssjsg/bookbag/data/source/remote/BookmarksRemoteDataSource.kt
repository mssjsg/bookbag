package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark
import github.io.mssjsg.bookbag.user.BookbagUserData
import io.reactivex.Completable
import io.reactivex.Single
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksRemoteDataSource @Inject constructor(firebaseDatabase: FirebaseDatabase,
                                                    userData: BookbagUserData)
    : RemoteDataSource<FirebaseBookmark, Bookmark>(firebaseDatabase, userData, "bookmarks") {

    override fun convertLocalToRemoteData(localData: Bookmark): FirebaseBookmark {
        return FirebaseBookmark.create(localData)
    }

    override fun getIdFromLocalData(localData: Bookmark): String {
        return localData.url
    }

    override fun createRemoteDataWithParentFolderId(remoteData: FirebaseBookmark, parentFolderId: String?): FirebaseBookmark {
        return remoteData.copy(folderId = parentFolderId)
    }

    override fun getIdFromRemoteData(remoteData: FirebaseBookmark): String {
        return remoteData.url
    }

    override fun convertRemoteToLocalData(remoteData: FirebaseBookmark): Bookmark {
        return remoteData.toLocalData()
    }

    override fun updateItem(bookmark: Bookmark): Single<String> {
        return saveItem(bookmark)
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseBookmark? {
        return dataSnapshot.getValue(FirebaseBookmark::class.java)
    }

    private fun getKey(url: String): String {
        return URLEncoder.encode(url, "UTF-8").replace(".", "%2E")
    }
}