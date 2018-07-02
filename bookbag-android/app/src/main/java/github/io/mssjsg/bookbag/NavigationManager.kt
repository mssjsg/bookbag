package github.io.mssjsg.bookbag

import android.support.annotation.AnimRes
import android.support.annotation.AnimatorRes

interface NavigationManager {
    fun addToBackStack(fragment: BookbagFragment, tag: String, @AnimatorRes @AnimRes enter: Int = 0,
                       @AnimatorRes @AnimRes exit: Int = 0)

    fun setCurrentFragment(fragment: BookbagFragment, @AnimatorRes @AnimRes enter: Int = 0,
                           @AnimatorRes @AnimRes exit: Int = 0)

    fun isFragmentAdded(tag: String): Boolean
}