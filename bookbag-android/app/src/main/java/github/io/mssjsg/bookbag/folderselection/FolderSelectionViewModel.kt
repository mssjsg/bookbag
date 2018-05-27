package github.io.mssjsg.bookbag.folderselection

import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class FolderSelectionViewModel @Inject constructor(application: BookBagApplication,
                                                   logger: Logger,
                                                   rxTransformers: RxTransformers,
                                                   liveBus: LiveBus,
                                                   localLiveBus: LocalLiveBus,
                                                   loadPreviewInteractor: LoadPreviewInteractor,
                                                   loadListItemsInteractor: LoadListItemsInteractor,
                                                   loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                                   getFolderInteractor: GetFolderInteractor) : ItemListViewModel(
        application, logger, rxTransformers, liveBus, localLiveBus, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, getFolderInteractor) {
    lateinit var folderSelectionComponent: FolderSelectionComponent
}