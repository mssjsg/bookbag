package github.io.mssjsg.bookbag.move

import android.arch.lifecycle.AndroidViewModel
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.list.MoveSelectedItemsEvent
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MoveViewModel @Inject constructor(application: BookBagApplication,
                                        bookmarksRepository: BookmarksRepository,
                                        foldersRepository: FoldersRepository,
                                        liveBus: LiveBus,
                                        localLiveBus: LocalLiveBus) : ItemListViewModel(application,
        bookmarksRepository, foldersRepository, liveBus, localLiveBus) {
}