package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark
import io.reactivex.Flowable
import java.net.URLEncoder
import javax.inject.Inject

class BookmarksRemoteDataSource @Inject constructor(firebaseDatabase: FirebaseDatabase): BaseRemoteDataSource<FirebaseBookmark>(firebaseDatabase, "bookmarks"), BookmarksDataSource {
    override fun getDirtyBookmarks(): Flowable<List<Bookmark>> {
        throw UnsupportedOperationException("not supported query dirty bookmarks")
    }

    override fun saveBookmark(bookmark: Bookmark) {
        val key = URLEncoder.encode(bookmark.url, "UTF-8").replace(".", "%2E")
        rootReference.child(key).setValue(FirebaseBookmark.create(bookmark))
    }

    override fun moveBookmark(url: String, folderId: Int?) {
        val databaseReference = rootReference.child(url)
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
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

    override fun updateBookmark(bookmark: Bookmark) {
        saveBookmark(bookmark)
    }

    override fun deleteBookmarks(bookmarkUrls: List<String>) {
        for (url in bookmarkUrls) {
            rootReference.child(url).removeValue()
        }
    }

    override fun getBookmarks(folderId: Int?): Flowable<List<Bookmark>> {
        throw UnsupportedOperationException("not supported query bookmarks by folder id")
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseBookmark? {
        return dataSnapshot.getValue(FirebaseBookmark::class.java)
    }
}