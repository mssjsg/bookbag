package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseFolder
import io.reactivex.Flowable
import javax.inject.Inject

class FoldersRemoteDataSource @Inject constructor(val firebaseDatabase: FirebaseDatabase): BaseRemoteDataSource<FirebaseFolder>(firebaseDatabase,
        "folders"), FoldersDataSource {
    override fun getDirtyFolders(): Flowable<List<Folder>> {
        throw UnsupportedOperationException("not supported query dirty folders")
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseFolder? {
        return dataSnapshot.getValue(FirebaseFolder::class.java)
    }

    override fun getFolders(folderId: Int?): Flowable<List<Folder>> {
        throw UnsupportedOperationException("not supported query folders by folder id")
    }

    override fun getCurrentFolder(folderId: Int): Flowable<Folder> {
        throw UnsupportedOperationException("not supported query folder by folder id")
    }

    override fun saveFolder(folder: Folder) {
        rootReference.child(folder.folderId.toString()).setValue(FirebaseFolder.create(folder))
    }

    override fun updateFolder(folder: Folder) {
        saveFolder(folder)
    }

    override fun moveFolder(folderId: Int, parentFolderId: Int?) {
        val databaseReference = rootReference.child(folderId.toString())
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
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

    override fun deleteFolders(folderIds: List<Int>) {
        for (id in folderIds) {
            rootReference.child(id.toString()).removeValue()
        }
    }
}