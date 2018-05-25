package github.io.mssjsg.bookbag.util.extension

import android.support.v4.app.Fragment
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.BookBagApplication

fun Fragment.getAppComponent(): BookBagAppComponent {
    return (context?.applicationContext as BookBagApplication).run { appComponent }
}