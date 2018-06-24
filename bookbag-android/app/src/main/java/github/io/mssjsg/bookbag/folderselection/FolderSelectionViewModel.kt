package github.io.mssjsg.bookbag.folderselection

import android.arch.lifecycle.MutableLiveData
import github.io.mssjsg.bookbag.folderselection.event.FolderSelectionEvent
import github.io.mssjsg.bookbag.interactor.itemlist.GetFolderInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadFolderPathsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadListItemsInteractor
import github.io.mssjsg.bookbag.interactor.itemlist.LoadPreviewInteractor
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

    val isFinished: MutableLiveData<Boolean> = MutableLiveData()
    var requestId: Int = -1

    fun onConfirmButtonClick() {
        liveBus.post(FolderSelectionEvent(requestId, true, currentFolderId))
        isFinished.value = true
    }

    fun onCancelButtonClick() {
        liveBus.post(FolderSelectionEvent(requestId, false))
        isFinished.value = true
    }

    override fun onBackPressed(): Boolean {
        if (!super.onBackPressed()) {
            onCancelButtonClick()
        }
        return true
    }
}