package github.io.mssjsg.bookbag.move

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.list.ItemListActivity
import github.io.mssjsg.bookbag.list.ItemListFragment
import github.io.mssjsg.bookbag.list.MoveSelectedItemsEvent
import github.io.mssjsg.bookbag.main.MainViewModel
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelFactory

class MoveActivity: ItemListActivity<MoveViewModel>() {
    companion object {
        const val TAG_ITEM_LIST = "github.io.mssjsg.bookbag.move.TAG_ITEM_LIST"
    }

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(MoveViewModel::class.java)

        supportActionBar?.title = getString(R.string.title_move_to)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_folder_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when(itemId) {
                R.id.item_confirm_folder_selection -> {
                    viewModel.liveBus.post(MoveSelectedItemsEvent(getItemListFragment().currentFolderId))
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateViewModel(): MoveViewModel {
        return ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(MoveViewModel::class.java)
    }
}