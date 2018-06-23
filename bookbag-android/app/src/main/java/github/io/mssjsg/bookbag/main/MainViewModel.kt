package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.AndroidViewModel
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.folderview.FolderViewComponent
import github.io.mssjsg.bookbag.util.Logger
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        logger: Logger) : AndroidViewModel(application) {
    lateinit var folderViewComponent: FolderViewComponent
}