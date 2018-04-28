package github.io.mssjsg.bookbag.util.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.main.MainViewModel
import github.io.mssjsg.bookbag.move.MoveViewModel

/**
 * Created by Sing on 27/3/2018.
 */
class ViewModelFactory(val bookmarkAppComponent: BookBagAppComponent): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when(modelClass) {
            MainViewModel::class.java -> return bookmarkAppComponent.mainComponent().provideMainViewModel() as T
//            MoveViewModel::class.java -> return bookmarkAppComponent.mainComponent().provideMainViewModel() as T

            else -> throw IllegalArgumentException("unknown ViewModel class: " + modelClass::class.java)
        }
        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            return bookmarkAppComponent.mainComponent().provideMainViewModel() as T
        } else

        throw IllegalArgumentException("unknown ViewModel class: " + modelClass::class.java)
    }
}