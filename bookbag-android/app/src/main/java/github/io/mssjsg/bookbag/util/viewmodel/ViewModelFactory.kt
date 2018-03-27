package github.io.mssjsg.bookbag.util.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.main.MainViewModel

/**
 * Created by Sing on 27/3/2018.
 */
class ViewModelFactory(val bookmarkAppComponent: BookBagAppComponent): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return bookmarkAppComponent.mainComponent().provideMainViewModel() as T
        }

        throw IllegalArgumentException("unknown ViewModel class: " + modelClass::class.java)
    }
}