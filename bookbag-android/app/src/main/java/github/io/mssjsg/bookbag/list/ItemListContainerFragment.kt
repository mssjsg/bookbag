package github.io.mssjsg.bookbag.list

import android.os.Bundle
import github.io.mssjsg.bookbag.BookbagFragment
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.folderselection.FolderSelectionFragment
import github.io.mssjsg.bookbag.util.extension.getFilteredFolderIds
import github.io.mssjsg.bookbag.util.extension.getFolderId

abstract class ItemListContainerFragment<VM : ItemListViewModel> : BookbagFragment(),
        ItemListViewModelProvider, ItemListViewModel.FolderViewer {
    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        viewModel.folderViewer = this
        arguments?.let { bundle ->
            viewModel.filteredFolders = bundle?.getFilteredFolderIds()
        }
        onViewModelCreated(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.folderViewer = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.getFolderId() != viewModel.currentFolderId
                    || !(childFragmentManager.findFragmentByTag(FolderSelectionFragment.TAG_ITEM_LIST) is ItemListFragment)) {
                showItemListFragment(bundle.getFolderId(), ItemListViewModel.TransitionType.FRESH)
            }
        }
    }

    protected abstract fun onCreateViewModel(): VM

    open protected fun onViewModelCreated(viewModel: VM) {}

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

    protected fun getItemListFragment(): ItemListFragment {
        return childFragmentManager.findFragmentByTag(FolderSelectionFragment.TAG_ITEM_LIST) as ItemListFragment
    }

    override fun showFolder(folderId: String?, transitionType: ItemListViewModel.TransitionType) {
        showItemListFragment(folderId, transitionType)
    }

    private fun showItemListFragment(folderId: String?, transitionType: ItemListViewModel.TransitionType) {
        childFragmentManager.beginTransaction().apply {
            when (transitionType) {
                ItemListViewModel.TransitionType.FORWARD -> {
                    setCustomAnimations(R.anim.forward_enter_animation, R.anim.forward_exit_animation)
                }
                ItemListViewModel.TransitionType.BACKWARD -> {
                    setCustomAnimations(R.anim.backward_enter_animation, R.anim.backward_exit_animation)
                }
                else -> {
                }
            }
        }.replace(R.id.list_container, ItemListFragment.newInstance(folderId),
                FolderSelectionFragment.TAG_ITEM_LIST)
                .commit()
    }

    override fun getItemListViewModel(): ItemListViewModel {
        return viewModel
    }
}