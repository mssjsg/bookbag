package github.io.mssjsg.bookbag.data.source.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseFolder
import github.io.mssjsg.bookbag.user.BookbagUserData
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoldersRemoteDataSource @Inject constructor(firebaseDatabase: FirebaseDatabase, userData: BookbagUserData): RemoteDataSource<FirebaseFolder, Folder>(firebaseDatabase,
        userData, "folders") {
    override fun createRemoteDataWithParentFolderId(remoteData: FirebaseFolder, parentFolderId: String?): FirebaseFolder {
        return remoteData.copy(parentFolderId = parentFolderId)
    }

    override fun convertLocalToRemoteData(localData: Folder): FirebaseFolder {
        return FirebaseFolder.create(localData)
    }

    override fun getIdFromLocalData(localData: Folder): String {
        return localData.folderId
    }

    override fun getIdFromRemoteData(remoteData: FirebaseFolder): String {
        return remoteData.folderId
    }

    override fun convertRemoteToLocalData(remoteData: FirebaseFolder): Folder {
        return remoteData.toLocalData()
    }

    override fun getItemFromSnapshot(dataSnapshot: DataSnapshot): FirebaseFolder? {
        return dataSnapshot.getValue(FirebaseFolder::class.java)
    }

    override fun updateItem(folder: Folder): Single<String> {
        return saveItem(folder)
    }
}