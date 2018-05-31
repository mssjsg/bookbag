package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.Observer
import android.os.Bundle
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.folderselection.FolderSelectionActivity
import github.io.mssjsg.bookbag.list.event.ItemClickEvent
import github.io.mssjsg.bookbag.list.event.PathClickEvent
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.extension.getFilteredFolderIds
import github.io.mssjsg.bookbag.util.extension.getFolderId

abstract class ItemListActivity<VM : ItemListViewModel> : BookbagActivity(), ItemListViewModelProvider {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        viewModel.filteredFolders = intent.getFilteredFolderIds()
        onViewModelCreated(viewModel)
        showItemListFragment(intent.getFolderId(), TransitionType.FRESH)

        viewModel.localLiveBus.let {
            it.subscribe(this, Observer {
                it?.let { onItemSelected(it.position) }
            }, ItemClickEvent::class)
            it.subscribe(this, Observer {
                it?.let { onPathSelected(it.folderId) }
            }, PathClickEvent::class)
        }
    }

    private fun onItemSelected(position: Int) {
        viewModel.items.get(position).let { item ->
            if (item is FolderListItem && !viewModel.isInMultiSelectionMode) {
                showItemListFragment(item.folderId, TransitionType.FORWARD)
            }
        }
    }

    private fun onPathSelected(folderId: String?) {
        folderId?.let {
            if (!it.isEmpty()) {
                showItemListFragment(folderId, TransitionType.BACKWARD)
            } else {
                showItemListFragment(null, TransitionType.BACKWARD)
            }
        }
    }

    protected abstract fun onCreateViewModel(): VM

    open protected fun onViewModelCreated(viewModel: VM) {}

    override fun onBackPressed() {
        if (viewModel.currentFolderId == null) {
            super.onBackPressed()
        } else {
            showItemListFragment(viewModel.parentFolderId, TransitionType.BACKWARD)
        }
    }

    protected fun getItemListFragment(): ItemListFragment {
        return supportFragmentManager.findFragmentByTag(FolderSelectionActivity.TAG_ITEM_LIST) as ItemListFragment
    }

    private fun showItemListFragment(folderId: String?, transitionType: TransitionType) {
        if (viewModel.currentFolderId != folderId
                || !(supportFragmentManager.findFragmentByTag(FolderSelectionActivity.TAG_ITEM_LIST) is ItemListFragment)) {
            supportFragmentManager.beginTransaction().apply {
                when (transitionType) {
                    TransitionType.FORWARD -> {
                        setCustomAnimations(R.anim.forward_enter_animation, R.anim.forward_exit_animation)
                    }
                    TransitionType.BACKWARD -> {
                        setCustomAnimations(R.anim.backward_enter_animation, R.anim.backward_exit_animation)
                    }
                    else -> {
                    }
                }
            }.replace(R.id.list_container, ItemListFragment.newInstance(folderId),
                    FolderSelectionActivity.TAG_ITEM_LIST)
                    .commit()
        }
    }

    override fun getItemListViewModel(): ItemListViewModel {
        return viewModel
    }

    private enum class TransitionType {
        FRESH, FORWARD, BACKWARD
    }
}