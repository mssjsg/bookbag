package github.io.mssjsg.bookbag.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.move.MoveActivity

abstract class ItemListActivity<VM: ItemListViewModel> : BookbagActivity() {
    companion object {
        const val EXTRA_FOLDER_ID = "github.io.mssjsg.bookbag.main.EXTRA_FOLDER_ID"
    }

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        addItemListFragment()
    }

    protected abstract fun onCreateViewModel(): VM

    fun onItemSelected(position: Int) {
        if (viewModel.isInActionMode) {
            viewModel.toggleSelected(position)
        } else {
            viewModel.getListItem(position).let { when(it) {
                is BookmarkListItem -> {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(it.url)
                    startActivity(i)
                }
                is FolderListItem -> {
                    viewModel.loadFolder(it.folderId)
                }
            }}
        }
    }

    override fun onBackPressed() {
        if(viewModel.currentFolderId == null) {
            super.onBackPressed()
        } else {
            viewModel.loadParentFolder()
        }
    }

    protected fun getItemListFragment(): ItemListFragment {
        return supportFragmentManager.findFragmentByTag(MoveActivity.TAG_ITEM_LIST) as ItemListFragment
    }

    private fun addItemListFragment() {
        if (!(supportFragmentManager.findFragmentByTag(MoveActivity.TAG_ITEM_LIST) is ItemListFragment)) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.list_container, ItemListFragment.newInstance(), MoveActivity.TAG_ITEM_LIST)
                    .commit()
        }
    }

}