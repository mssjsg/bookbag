package github.io.mssjsg.bookbag.intro

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.user.GoogleAuthHelper
import javax.inject.Inject

class IntroViewModel @Inject constructor(application: BookBagApplication,
                                         val googleAuthHelper: GoogleAuthHelper,
                                         val userData: BookbagUserData) : AndroidViewModel(application) {

    val isLoading: LiveData<Boolean>
        get() = googleAuthHelper.isLoading

    fun signIn() {
        googleAuthHelper.signIn()
    }
}