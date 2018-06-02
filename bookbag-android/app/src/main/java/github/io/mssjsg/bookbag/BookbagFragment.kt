package github.io.mssjsg.bookbag

import android.support.v4.app.Fragment

abstract class BookbagFragment: Fragment() {

    protected val navigationManager: NavigationManager?
        get() = if (activity is NavigationManager) activity as NavigationManager else null

    protected fun getAppComponent(): BookBagAppComponent {
        return (activity?.application as BookBagApplication).appComponent
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    abstract fun isSignInRequired(): Boolean
}