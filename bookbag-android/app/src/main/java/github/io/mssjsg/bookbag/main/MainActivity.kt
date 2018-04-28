package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import github.io.mssjsg.bookbag.BookbagActivity
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.list.*
import github.io.mssjsg.bookbag.move.MoveActivity
import github.io.mssjsg.bookbag.util.getSharedUrl
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelFactory
import github.io.mssjsg.bookbag.widget.SimpleInputDialogFragment

class MainActivity : ItemListActivity<MainViewModel>(), ActionMode.Callback, ItemListViewModelProvider {
    companion object {
        private const val REQUEST_ID_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.REQUEST_ID_CREATE_NEW_FOLDER"
        private const val TAG_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.TAG_CREATE_NEW_FOLDER"
    }

    private var actionMode: ActionMode? = null
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.localLiveBus.let {
            //init event
            it.subscribe(this, Observer {
                actionMode = startActionMode(this)
            }, ItemLongClickEvent::class)
        }

        viewModel.liveBus.let {
            it.subscribe(this, Observer {
                it?.apply {
                    when(requestId) {
                        REQUEST_ID_CREATE_NEW_FOLDER -> viewModel.addFolder(Folder(name = input))
                    }
                }
            }, SimpleInputDialogFragment.ConfirmEvent::class)
        }
    }

    override fun onCreateViewModel(): MainViewModel {
        return ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when(itemId) {
                R.id.item_new_folder -> {
                    SimpleInputDialogFragment.newInstance(REQUEST_ID_CREATE_NEW_FOLDER,
                            hint = getString(R.string.hint_folder_name),
                            title = getString(R.string.title_new_folder))
                            .show(supportFragmentManager, TAG_CREATE_NEW_FOLDER)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        detectNewUrl(intent)
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                viewModel.deleteSelectedItems()
                mode?.finish()
                true
            }
            R.id.item_move -> {
                val intent = Intent(this, MoveActivity::class.java)
                intent.putExtra(EXTRA_FOLDER_ID, viewModel.currentFolderId)
                startActivity(intent)
            }
            else -> false
        }

        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_main_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        viewModel.isInActionMode = true
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        viewModel.isInActionMode = false
    }

    override fun getItemListViewModel(): ItemListViewModel {
        return viewModel
    }

    private fun detectNewUrl(intent: Intent?) {
        val newUrl = intent?.getSharedUrl()
        if (!newUrl.isNullOrBlank()) {
            viewModel.addBookmark(Bookmark(url = newUrl ?: ""))
        }
    }
}
