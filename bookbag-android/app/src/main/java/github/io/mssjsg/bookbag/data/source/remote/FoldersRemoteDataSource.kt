package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseFolder
import github.io.mssjsg.bookbag.user.BookbagUserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoldersRemoteDataSource @Inject constructor(firebaseDatabase: FirebaseDatabase, userData: BookbagUserData): RemoteDataSource<FirebaseFolder, Folder>(firebaseDatabase,
        userData, "folders") {
    override fun getIdFromRemoteData(remoteData: FirebaseFolder): String {
        return remoteData.folderId
    }

    override fun convertRemoteToLocalData(remoteData: FirebaseFolder): Folder {
        return remoteData.toLocalData()
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseFolder? {
        return dataSnapshot.getValue(FirebaseFolder::class.java)
    }

    override fun saveItem(folder: Folder) {
        rootReference?.child(folder.folderId)?.setValue(FirebaseFolder.create(folder))
    }

    override fun updateItem(folder: Folder) {
        saveItem(folder)
    }

    override fun moveItem(folderId: String, parentFolderId: String?) {
        val databaseReference = rootReference?.child(folderId.toString())
        databaseReference?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                dataSnapshot?.let {
                    val firebaseFolder = getItemFromSnapshot(dataSnapshot)
                    firebaseFolder?.let {
                        val newFirebaseFolder = firebaseFolder.copy(folderId = folderId)
                        databaseReference.setValue(newFirebaseFolder)
                    }
                }
            }

        });
    }

    override fun deleteItems(folderIds: List<String>) {
        for (id in folderIds) {
            rootReference?.child(id)?.removeValue()
        }
    }
}