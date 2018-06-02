package github.io.mssjsg.bookbag.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.BookbagFragment
import github.io.mssjsg.bookbag.NavigationManager
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.folderview.FolderViewFragment
import github.io.mssjsg.bookbag.intro.IntroFragment
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.extension.getSharedUrl
import javax.inject.Inject


class MainActivity : BookbagActivity(), NavigationManager {
    @Inject
    lateinit var bookbagUserData: BookbagUserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAppComponent().mainComponent().inject(this)

        setContentView(R.layout.activity_main)
        tryAttachFragment(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        tryAttachFragment(intent)
    }

    override fun onBackPressed() {
        getCurrentFragment()?.let {
            if (!it.onBackPressed()) {
                super.onBackPressed()
            }
        } ?: run {
            super.onBackPressed()
        }
    }

    override fun addToBackStack(fragment: BookbagFragment, tag: String, enter: Int, exit: Int) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(enter, exit, enter, exit)
                .add(R.id.container, fragment)
                .addToBackStack(tag).commit()
    }

    private fun tryAttachFragment(intent: Intent?) {
        var newFragment: BookbagFragment? = null
        val sharedUrl = intent?.getSharedUrl() ?: ""
        val isSignedIn = bookbagUserData.isSignedIn
        if (sharedUrl.isNotEmpty()) {
            newFragment = FolderViewFragment.newInstance().apply {
                addBookmark(sharedUrl)
            }
        } else {
            var currentFragment = getCurrentFragment()
            currentFragment?.let {
                if (!isSignedIn && it.isSignInRequired()) {
                    newFragment = IntroFragment.newInstance()
                }
            } ?: run {
                if (isSignedIn) {
                    newFragment = FolderViewFragment.newInstance()
                } else {
                    newFragment = IntroFragment.newInstance()
                }
            }
        }
        newFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.container, it).commit()
        }
    }

    override fun setCurrentFragment(fragment: BookbagFragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun getCurrentFragment(): BookbagFragment? {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is BookbagFragment) {
            return fragment
        }

        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
