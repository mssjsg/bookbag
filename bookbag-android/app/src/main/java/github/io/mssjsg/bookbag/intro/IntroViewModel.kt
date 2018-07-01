package github.io.mssjsg.bookbag.intro

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.folderview.FolderViewFragment
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.user.GoogleAuthHelper
import javax.inject.Inject

class IntroViewModel @Inject constructor(application: BookBagApplication,
                                         val googleAuthHelper: GoogleAuthHelper,
                                         private val userData: BookbagUserData) : AndroidViewModel(application) {

    private val isLoading: LiveData<Boolean>
        get() = googleAuthHelper.isLoading

    val isGoogleSignInButtonVisible: LiveData<Boolean>
        get() = isLoading

    val isProgressVisible: LiveData<Boolean>
        get() = isLoading

    private val _isFinished: MutableLiveData<Boolean> = MutableLiveData()
    val isFinished: LiveData<Boolean> = _isFinished

    private val _isSignInLaterButtonVisible: MutableLiveData<Boolean> = MutableLiveData()
    val isSignInLaterButtonVisible: LiveData<Boolean> = _isSignInLaterButtonVisible

    fun onSignInWithGoogleButtonClick() {
        googleAuthHelper.signIn()
    }

    fun onSignInLaterButtonClick() {
        _isFinished.value = true
    }

    init {
        userData.observeForever(Observer {
            it?.let {
                _isFinished.value = true
            }
        })
    }
}