package github.io.mssjsg.bookbag.util.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.folderselection.FolderSelectionViewModel
import github.io.mssjsg.bookbag.main.MainViewModel

/**
 * Created by Sing on 27/3/2018.
 */
class ViewModelFactory(val bookmarkAppComponent: BookBagAppComponent): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when(modelClass) {
            MainViewModel::class.java -> return bookmarkAppComponent.viewModelComponent().provideMainViewModel() as T
            FolderSelectionViewModel::class.java -> return bookmarkAppComponent.viewModelComponent().provideFolderSelectionViewModel() as T

            else -> throw IllegalArgumentException("unknown ViewModel class: " + modelClass::class.java)
        }
    }
}