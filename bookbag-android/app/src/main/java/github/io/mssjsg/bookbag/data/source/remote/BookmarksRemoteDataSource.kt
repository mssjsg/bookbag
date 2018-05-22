package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark
import github.io.mssjsg.bookbag.user.BookbagUserData
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarksRemoteDataSource @Inject constructor(firebaseDatabase: FirebaseDatabase,
                                                    userData: BookbagUserData)
    : RemoteDataSource<FirebaseBookmark, Bookmark>(firebaseDatabase, userData, "bookmarks") {
    override fun getIdFromRemoteData(remoteData: FirebaseBookmark): String {
        return remoteData.url
    }

    override fun convertRemoteToLocalData(remoteData: FirebaseBookmark): Bookmark {
        return remoteData.toLocalData()
    }

    override fun saveItem(bookmark: Bookmark) {
        rootReference?.child(getKey(bookmark.url))?.setValue(FirebaseBookmark.create(bookmark))
    }

    override fun moveItem(url: String, folderId: String?) {
        val databaseReference = rootReference?.child(url)
        databaseReference?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                dataSnapshot?.let {
                    val firebaseBookmark = getItemFromSnapshot(dataSnapshot)
                    firebaseBookmark?.let {
                        val newFirebaseBookmark = firebaseBookmark.copy(folderId = folderId)
                        databaseReference.setValue(newFirebaseBookmark)
                    }
                }
            }
        });
    }

    override fun updateItem(bookmark: Bookmark) {
        saveItem(bookmark)
    }

    override fun deleteItems(bookmarkUrls: List<String>) {
        for (url in bookmarkUrls) {
            rootReference?.child(getKey(url))?.removeValue()
        }
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseBookmark? {
        return dataSnapshot.getValue(FirebaseBookmark::class.java)
    }

    private fun getKey(url: String): String {
        return URLEncoder.encode(url, "UTF-8").replace(".", "%2E")
    }
}