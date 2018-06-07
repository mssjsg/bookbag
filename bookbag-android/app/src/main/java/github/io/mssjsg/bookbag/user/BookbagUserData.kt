package github.io.mssjsg.bookbag.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.v4.util.ArraySet
import androidx.core.util.arraySetOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookbagUserData @Inject constructor(): LiveData<FirebaseUser>() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun getValue(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    val isSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    val userId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                postValue(firebaseAuth.currentUser)
            } ?: run {
                postValue(null)
            }
        }
    }
}