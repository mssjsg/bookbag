package github.io.mssjsg.bookbag.folderselection

import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.itemlist.DeleteFolderInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadFolderPathsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadListItemsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadPreviewInteractor
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.user.BookbagUserData
import github.io.mssjsg.bookbag.util.BookbagSchedulers
import github.io.mssjsg.bookbag.util.ItemUidGenerator
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class FolderSelectionViewModel @Inject constructor(application: BookBagApplication,
                                                   schedulers: BookbagSchedulers,
                                                   logger: Logger,
                                                   bookmarksRepository: BookmarksRepository,
                                                   foldersRepository: FoldersRepository,
                                                   liveBus: LiveBus,
                                                   localLiveBus: LocalLiveBus,
                                                   uidGenerator: ItemUidGenerator,
                                                   bookbagUserData: BookbagUserData,
                                                   loadPreviewInteractor: LoadPreviewInteractor,
                                                   loadListItemsInteractor: LoadListItemsInteractor,
                                                   loadFolderPathsInteractor: LoadFolderPathsInteractor,
                                                   deleteFolderInteractor: DeleteFolderInteractor) : ItemListViewModel(
        application, schedulers, logger, bookmarksRepository, foldersRepository, liveBus, localLiveBus,
        uidGenerator, bookbagUserData, loadPreviewInteractor, loadListItemsInteractor, loadFolderPathsInteractor,
        deleteFolderInteractor) {

    lateinit var folderSelectionComponent: FolderSelectionComponent
}