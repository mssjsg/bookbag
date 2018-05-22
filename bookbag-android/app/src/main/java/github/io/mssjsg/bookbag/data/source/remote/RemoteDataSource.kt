package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.*
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.user.BookbagUserData
import io.reactivex.Flowable
import java.util.concurrent.CopyOnWriteArraySet

abstract class RemoteDataSource<RemoteData, LocalData>(val firebaseDatabase: FirebaseDatabase,
                                                       val userData: BookbagUserData,
                                                       val root: String): BookbagDataSource<LocalData> {

    val listeners: MutableSet<OnRemoteDataChangedListener<RemoteData>> = CopyOnWriteArraySet()

    protected val databaseReference: DatabaseReference

    protected var rootReference: DatabaseReference? = null

    private val childListener: ChildListener = ChildListener()

    init {
        databaseReference = firebaseDatabase.reference
        userData.observeForever({
            if (userData.isSignedIn) {
                rootReference = firebaseDatabase.getReference(String.format("users/%s/%s", userData.userId, root))
                rootReference?.addChildEventListener(childListener)
            } else {
                rootReference?.removeEventListener(childListener)
                rootReference = null
            }
        })
    }

    protected abstract fun getItemFromSnapshot(dataSnapshot: DataSnapshot): RemoteData?

    abstract fun convertRemoteToLocalData(remoteData: RemoteData): LocalData

    abstract fun getIdFromRemoteData(remoteData: RemoteData): String

    private inner class ChildListener : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            getItemFromSnapshot(dataSnapshot)?.let {
                for (listener in listeners) listener.onItemAdded(it)
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            getItemFromSnapshot(dataSnapshot)?.let {
                for (listener in listeners) listener.onItemUpdated(it)
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            getItemFromSnapshot(dataSnapshot)?.let {
                for (listener in listeners) listener.onItemRemoved(it)
            }
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            //do nothing
        }

        override fun onCancelled(databaseError: DatabaseError) {
            //do nothing
        }
    }

    override final fun getItem(id: String): Flowable<LocalData> {
        throw UnsupportedOperationException("not supported query item")
    }

    override final fun getDirtyItems(): Flowable<List<LocalData>> {
        throw UnsupportedOperationException("not supported query dirty items")
    }

    override final fun getItems(folderId: String?): Flowable<List<LocalData>> {
        throw UnsupportedOperationException("not supported query items by id")
    }

    interface OnRemoteDataChangedListener<DataType> {
        fun onItemAdded(data: DataType)

        fun onItemRemoved(data: DataType)

        fun onItemUpdated(data: DataType)
    }
}