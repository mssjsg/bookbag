package github.io.mssjsg.bookbag.intro

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.user.GoogleAuthHelper
import github.io.mssjsg.bookbag.util.extension.observeForeverNonNull
import javax.inject.Inject

class IntroViewModel @Inject constructor(val googleAuthHelper: GoogleAuthHelper,
                                         private val userData: BookbagUserData) : ViewModel() {

    private val _isGoogleSignInButtonVisible: MutableLiveData<Boolean> = MutableLiveData()
    private val _isProgressVisible: MutableLiveData<Boolean> = MutableLiveData()
    private val _isFinished: MutableLiveData<Boolean> = MutableLiveData()
    private val _isSignInLaterButtonVisible: MutableLiveData<Boolean> = MutableLiveData()

    val isGoogleSignInButtonVisible: LiveData<Boolean> get() = _isGoogleSignInButtonVisible
    val isProgressVisible: LiveData<Boolean> get() = _isProgressVisible
    val isFinished: LiveData<Boolean> get() = _isFinished
    val isSignInLaterButtonVisible: LiveData<Boolean> get() = _isSignInLaterButtonVisible

    fun onSignInWithGoogleButtonClick() {
        userData.isInOfflineMode = false
        googleAuthHelper.signIn()
    }

    fun onSignInLaterButtonClick() {
        userData.isInOfflineMode = true
        _isFinished.value = true
    }

    init {
        userData.observeForeverNonNull {
            _isFinished.value = true
        }

        googleAuthHelper.isLoading.observeForever {
            val isLoading: Boolean = it?.apply { this } ?: false

            _isGoogleSignInButtonVisible.value = !isLoading
            _isSignInLaterButtonVisible.value = !isLoading
            _isProgressVisible.value = isLoading
        }
    }
}