package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.*
import java.util.concurrent.CopyOnWriteArraySet

abstract class BaseRemoteDataSource<DataType>(firebaseDatabase: FirebaseDatabase, val root: String) {
    protected val databaseReference: DatabaseReference
    protected val rootReference: DatabaseReference

    val listeners: MutableSet<OnRemoteDataChangedListener<DataType>> = CopyOnWriteArraySet()

    init {
        databaseReference = firebaseDatabase.reference
        rootReference = firebaseDatabase.getReference(root)
        rootReference.addChildEventListener(ChildListener())
    }

    protected abstract fun getItemFromSnapshot(dataSnapshot: DataSnapshot): DataType?

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

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    interface OnRemoteDataChangedListener<DataType> {
        fun onItemAdded(data: DataType)

        fun onItemRemoved(data: DataType)

        fun onItemUpdated(data: DataType)
    }
}