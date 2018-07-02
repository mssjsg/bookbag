package github.io.mssjsg.bookbag.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.util.ArraySet
import androidx.core.util.arraySetOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookbagUserData @Inject constructor(application: BookBagApplication): LiveData<FirebaseUser>() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun signOut() {
        firebaseAuth.signOut()
    }

    val isSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    val userId: String?
        get() = firebaseAuth.currentUser?.uid

    private val sharePref = application.getSharedPreferences("setting", Context.MODE_PRIVATE)

    var isInOfflineMode: Boolean
        get() {
            return sharePref.getBoolean(IS_OFFLINE_MODE, false)
        }
        set(isInOfflineMode) {
            sharePref.edit().putBoolean(IS_OFFLINE_MODE, isInOfflineMode).commit()
        }

    init {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                postValue(firebaseAuth.currentUser)
            } ?: run {
                postValue(null)
            }
        }
    }

    private companion object {
        private const val IS_OFFLINE_MODE = "IS_OFFLINE_MODE"
    }
}