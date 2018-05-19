package github.io.mssjsg.bookbag.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Responsible for listening for auth events and sync the data to db
 */
@Singleton
class BookbagUserManager @Inject constructor(val foldersRepository: FoldersRepository, val bookmarksRepository: BookmarksRepository) {

    init {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let {
                foldersRepository.synchronizeToRemote()
                bookmarksRepository.synchronizeToRemote()
            }
        }

        foldersRepository.getDirtyFolders().subscribe({ folders ->
            if (isSignedIn()) {
                foldersRepository.synchronizeToRemote(folders)
            }
        })

        bookmarksRepository.getDirtyBookmarks().subscribe({ bookmarks ->
            if (isSignedIn()) {
                bookmarksRepository.synchronizeToRemote(bookmarks)
            }
        })
    }

    fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    interface OnAuthStateChangeListener {
        fun onSignIn(firebaseUser: FirebaseUser);

        fun onSignOut();
    }
}