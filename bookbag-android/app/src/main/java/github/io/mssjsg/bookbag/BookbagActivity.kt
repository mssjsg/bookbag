package github.io.mssjsg.bookbag

import android.support.v7.app.AppCompatActivity

abstract class BookbagActivity: AppCompatActivity() {
    protected fun getAppComponent(): BookBagAppComponent {
        return (application as BookBagApplication).appComponent
    }
}