package github.io.mssjsg.bookbag.folderselection

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
import github.io.mssjsg.bookbag.list.ItemListActivity
import github.io.mssjsg.bookbag.util.putFolderId

class FolderSelectionActivity: ItemListActivity<FolderSelectionViewModel>() {
    companion object {
        const val TAG_ITEM_LIST = "github.io.mssjsg.bookbag.move.TAG_ITEM_LIST"
    }

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.title = getString(R.string.title_move_to)
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
            when(itemId) {
                R.id.item_confirm_folder_selection -> {
                    val intent = Intent()
                    intent.putFolderId(viewModel.currentFolderId)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateViewModel(): FolderSelectionViewModel {
        return ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(FolderSelectionViewModel::class.java)
    }

    private class ViewModelFactory(val viewModelComponent: BookBagAppComponent): ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModelComponent.folderSelectionComponent().let {
                it.provideFolderSelectionViewModel().apply { this.folderSelectionComponent = folderSelectionComponent } as T
            }
        }
    }
}