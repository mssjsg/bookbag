package github.io.mssjsg.bookbag.main

import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.itemlist.LoadPreviewInteractor
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.BookbagSchedulers
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.linkpreview.UrlPreviewManager
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        schedulers: BookbagSchedulers,
                                        logger: Logger,
                                        bookmarksRepository: BookmarksRepository,
                                        foldersRepository: FoldersRepository,
                                        liveBus: LiveBus,
                                        localLiveBus: LocalLiveBus,
                                        uidGenerator: ItemUidGenerator,
                                        bookbagUserData: BookbagUserData,
                                        loadPreviewInteractor: LoadPreviewInteractor) : ItemListViewModel(
        application, schedulers, logger, bookmarksRepository, foldersRepository, liveBus, localLiveBus,
        uidGenerator, bookbagUserData, loadPreviewInteractor) {
    lateinit var mainComponent: MainComponent
}