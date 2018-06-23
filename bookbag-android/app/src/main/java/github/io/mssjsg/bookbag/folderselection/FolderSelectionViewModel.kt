package github.io.mssjsg.bookbag.folderselection

import android.databinding.ObservableBoolean
import github.io.mssjsg.bookbag.folderselection.event.FolderSelectionEvent
import github.io.mssjsg.bookbag.interactor.itemlist.*
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.util.Logger
import github.io.mssjsg.bookbag.util.RxTransformers
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import javax.inject.Inject

class FolderSelectionViewModel @Inject constructor(logger: Logger,
                                                   rxTransformers: RxTransformers,
                                                   liveBus: LiveBus,
                                                   loadPreviewInteractor: LoadPreviewInteractor,
                                                   loadListItemsInteractor: LoadListItemsInteractor,
                                                   loadFoldersPathsInteractor: LoadFolderPathsInteractor,
                                                   getFolderInteractor: GetFolderInteractor) : ItemListViewModel(
        logger, rxTransformers, loadPreviewInteractor,
        loadListItemsInteractor, loadFoldersPathsInteractor, getFolderInteractor) {
    lateinit var folderSelectionComponent: FolderSelectionComponent

    private val liveBus = liveBus

    val isFinished: ObservableBoolean = ObservableBoolean()
    var requestId: Int = -1

    fun onConfirmButtonClick() {
        liveBus.post(FolderSelectionEvent(requestId, true, currentFolderId))
        isFinished.set(true)
    }

    fun onCancelButtonClick() {
        liveBus.post(FolderSelectionEvent(requestId, false))
    }

    override fun onBackPressed(): Boolean {
        if (!super.onBackPressed()) {
            onCancelButtonClick()
        }
        return true
    }
}