package github.io.mssjsg.bookbag.user

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import github.io.mssjsg.bookbag.R
import javax.inject.Inject

class GoogleAuthHelper @Inject constructor() {
    private var googleSignInClient: GoogleSignInClient? = null

    private var auth: FirebaseAuth

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading

    var fragment:Fragment? = null
        set(value) {
            value?.activity?.let { activity ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(value.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                googleSignInClient = GoogleSignIn.getClient(activity, gso)
            }
            field = value
        }

    init {
        auth = FirebaseAuth.getInstance();
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount): Task<AuthResult> {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        return auth.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(@NonNull task: Task<AuthResult>) {
                        _isLoading.value = false
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success")
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException())
                        }
                    }
                })
    }

    fun signIn() {
        val signInIntent = googleSignInClient?.getSignInIntent()
        fragment?.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient?.signOut()
    }

    private fun revokeAccess() {
        auth.signOut()
        googleSignInClient?.revokeAccess()
    }

    companion object {
        const val TAG = "SignInHelper"
        private val RC_SIGN_IN = 9001
    }
}