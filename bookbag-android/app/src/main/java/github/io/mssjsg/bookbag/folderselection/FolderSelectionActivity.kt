package github.io.mssjsg.bookbag.folderselection

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.databinding.ActivitySelectFolderBinding
import github.io.mssjsg.bookbag.list.ItemListActivity
import github.io.mssjsg.bookbag.util.extension.putFilteredFolderIds
import github.io.mssjsg.bookbag.util.extension.putFolderId

class FolderSelectionActivity: ItemListActivity<FolderSelectionViewModel>() {
    private lateinit var mainBinding: ActivitySelectFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_folder)
        supportActionBar?.title = getTitle(this)
        mainBinding.btnConfirm.text = getConfirmButtonText(this)

        mainBinding.btnConfirm.setOnClickListener({
            val intent = Intent()
            intent.putFolderId(viewModel.currentFolderId)
            setResult(RESULT_OK, intent)
            finish()
        })

        mainBinding.btnCanecl.setOnClickListener({
            finish()
        })
    }

    override fun onViewModelCreated(viewModel: FolderSelectionViewModel) {
        super.onViewModelCreated(viewModel)
        viewModel.isShowingBookmarks = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_folder_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateViewModel(): FolderSelectionViewModel {
        return ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(FolderSelectionViewModel::class.java)
    }

    private class ViewModelFactory(val viewModelComponent: BookBagAppComponent): ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModelComponent.folderSelectionComponent().let { component ->
                component.provideFolderSelectionViewModel().apply { this.folderSelectionComponent = component } as T
            }
        }
    }

    companion object {
        const val TAG_ITEM_LIST = "github.io.mssjsg.bookbag.folderselection.TAG_ITEM_LIST"

        const val EXTRA_TITLE = "github.io.mssjsg.bookbag.folderselection.EXTRA_TITLE"
        const val EXTRA_CONFIRM_BUTTON = "github.io.mssjsg.bookbag.folderselection.EXTRA_CONFIRM_BUTTON"

        fun startForResult(requestId:Int, activity: Activity, folderId: String?, filteredFolderIds: Array<String>, title: String, confirmButton: String) {
            activity.startActivityForResult(Intent(activity, FolderSelectionActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CONFIRM_BUTTON, confirmButton)
                putFolderId(folderId)
                putFilteredFolderIds(filteredFolderIds)
            }, requestId)
        }

        private fun getTitle(activity: FolderSelectionActivity): String {
            return activity.intent.getStringExtra(EXTRA_TITLE)
        }

        private fun getConfirmButtonText(activity: FolderSelectionActivity): String {
            return activity.intent.getStringExtra(EXTRA_CONFIRM_BUTTON)
        }
    }
}