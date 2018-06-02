package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.AndroidViewModel
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.folderview.FolderViewComponent
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        logger: Logger) : AndroidViewModel(application) {
    lateinit var folderViewComponent: FolderViewComponent
}