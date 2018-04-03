package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.util.getSharedUrl
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainListAdapter: MainListAdapter
    private lateinit var actionModeCallback: ActionModeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionModeCallback = ActionModeCallback()

        //init view model
        viewModel = ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(MainViewModel::class.java)
        viewModel.setLifecycleOwner(this)

        //init event
        viewModel.liveBus.subscribe(this, Observer {
            startActionMode(actionModeCallback)
            viewModel.setSelected(it!!.position, true)
        }, ItemLongClickEvent::class)

        viewModel.liveBus.subscribe(this, Observer {
            if (viewModel.isInActionMode) {
                it?.let { viewModel.toggleSelected(it.position) }
            } else {
                //TODO go to url
            }
        }, ItemClickEvent::class)

        //init adapter
        mainListAdapter = MainListAdapter(viewModel)

        //init bind views
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.viewmodel = viewModel
        mainBinding.bookmarksList.layoutManager = LinearLayoutManager(this)
        mainBinding.bookmarksList.adapter = mainListAdapter
        mainBinding.bookmarksList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        detectNewUrl(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        detectNewUrl(intent)
    }

    private fun getAppComponent(): BookBagAppComponent {
        return (application as BookBagApplication).appComponent
    }

    private fun detectNewUrl(intent: Intent?) {
        val newUrl = intent?.getSharedUrl()
        if (!newUrl.isNullOrBlank()) {
            viewModel.addBookmark(Bookmark(url = newUrl ?: ""))
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_delete -> {
                    viewModel.deleteSelectedItems()
                    mode?.finish()
                    true
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

    }
}
