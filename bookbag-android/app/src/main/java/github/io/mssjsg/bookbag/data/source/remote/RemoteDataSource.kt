package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.*
import github.io.mssjsg.bookbag.data.source.BookbagDataSource
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.extension.encodeForFirebaseKey
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
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

    override final fun saveItem(localData: LocalData): Single<String> {
        val key = getIdFromLocalData(localData).encodeForFirebaseKey()
        return Single.create({ emitter ->
            rootReference?.child(key)?.setValue(convertLocalToRemoteData(localData))
                    ?.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    emitter.onSuccess(key)
                } else {
                    task.exception?.let { emitter.onError(it) }
                }
            })
        })
    }

    override final fun moveItem(itemId: String, parentFolderId: String?): Single<Int> {
        val key = itemId.encodeForFirebaseKey()
        return Single.create({ emitter ->
            val databaseReference = rootReference?.child(key)
            databaseReference?.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError?) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    dataSnapshot?.let {
                        val firebaseItem = getItemFromSnapshot(dataSnapshot)
                        firebaseItem?.let {
                            val newFirebaseItem = createRemoteDataWithParentFolderId(firebaseItem, parentFolderId)
                            databaseReference.setValue(newFirebaseItem).addOnCompleteListener({ task ->
                                if (task.isSuccessful) {
                                    emitter.onSuccess(1)
                                } else {
                                    task.exception?.let { emitter.onError(it) }
                                }
                            })
                        }
                    }
                }
            });
        })
    }

    override final fun deleteItems(itemsIds: List<String>): Single<Int> {
        return Observable.fromIterable(itemsIds).flatMap({ id ->
            val key = id.encodeForFirebaseKey()
            Observable.create<Boolean>({ emitter ->
                rootReference?.child(key)?.removeValue()?.addOnCompleteListener({
                    emitter.onNext(it.isSuccessful)
                    emitter.onComplete()
                })
            })
        }).filter({ it }).toList().map { it -> it.size }
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

    abstract fun getItemFromSnapshot(dataSnapshot: DataSnapshot): RemoteData?

    abstract fun convertRemoteToLocalData(remoteData: RemoteData): LocalData

    abstract fun convertLocalToRemoteData(localData: LocalData): RemoteData

    abstract fun getIdFromRemoteData(remoteData: RemoteData): String

    abstract fun getIdFromLocalData(localData: LocalData): String

    abstract fun createRemoteDataWithParentFolderId(remoteData: RemoteData, parentFolderId: String?): RemoteData

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

    interface OnRemoteDataChangedListener<DataType> {
        fun onItemAdded(data: DataType)

        fun onItemRemoved(data: DataType)

        fun onItemUpdated(data: DataType)
    }
}