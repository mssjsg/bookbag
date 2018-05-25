package github.io.mssjsg.bookbag.list

import android.os.Bundle
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.folderselection.FolderSelectionActivity
import github.io.mssjsg.bookbag.util.extension.getFilteredFolderIds
import github.io.mssjsg.bookbag.util.extension.getFolderId

abstract class ItemListActivity<VM: ItemListViewModel> : BookbagActivity(), ItemListViewModelProvider {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        viewModel.filteredFolders = intent.getFilteredFolderIds()
        viewModel.currentFolderId = intent.getFolderId()
        onViewModelCreated(viewModel)
        addItemListFragment()
    }

    protected abstract fun onCreateViewModel(): VM

    open protected fun onViewModelCreated(viewModel: VM) {}

    override fun onBackPressed() {
        if(viewModel.currentFolderId == null) {
            super.onBackPressed()
        } else {
            viewModel.loadParentFolder()
        }
    }

    protected fun getItemListFragment(): ItemListFragment {
        return supportFragmentManager.findFragmentByTag(FolderSelectionActivity.TAG_ITEM_LIST) as ItemListFragment
    }

    private fun addItemListFragment() {
        if (!(supportFragmentManager.findFragmentByTag(FolderSelectionActivity.TAG_ITEM_LIST) is ItemListFragment)) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.list_container, ItemListFragment.newInstance(), FolderSelectionActivity.TAG_ITEM_LIST)
                    .commit()
        }
    }

    override fun getItemListViewModel(): ItemListViewModel {
        return viewModel
    }
}