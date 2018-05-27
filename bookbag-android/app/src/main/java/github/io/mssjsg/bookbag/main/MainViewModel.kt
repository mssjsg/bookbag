package github.io.mssjsg.bookbag.main

import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.RxSchedulers
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        logger: Logger,
                                        rxTransformers: RxTransformers,
                                        liveBus: LiveBus,
                                        localLiveBus: LocalLiveBus,
                                        loadPreviewInteractor: LoadPreviewInteractor,
                                        loadListItemsInteractor: LoadListItemsInteractor,
                                        loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                        deleteFolderInteractor: DeleteFolderInteractor,
                                        getFolderInteractor: GetFolderInteractor,
                                        addBookmarkInteractor: AddBookmarkInteractor,
                                        addFolderInteractor: AddFolderInteractor,
                                        moveItemsInteractor: MoveItemsInteractor) : ItemListViewModel(
        application, logger, rxTransformers, liveBus, localLiveBus, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, deleteFolderInteractor,
        getFolderInteractor, addBookmarkInteractor, addFolderInteractor, moveItemsInteractor
) {
    lateinit var mainComponent: MainComponent
}