package github.io.mssjsg.bookbag.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.view.*
import com.firebase.ui.auth.AuthUI
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.list.*
import github.io.mssjsg.bookbag.folderselection.FolderSelectionActivity
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.util.getFolderId
import github.io.mssjsg.bookbag.util.getSharedUrl
import github.io.mssjsg.bookbag.util.putFilteredFolderIds
import github.io.mssjsg.bookbag.util.putFolderId
import github.io.mssjsg.bookbag.widget.SimpleConfirmDialogFragment
import github.io.mssjsg.bookbag.widget.SimpleInputDialogFragment
import java.util.*
import android.util.Log
import com.firebase.ui.auth.IdpResponse


class MainActivity : ItemListActivity<MainViewModel>(), ActionMode.Callback {
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

            it.subscribe(this, Observer {
                it?.let { onItemSelected(it.position) }
            }, ItemClickEvent::class)

            it.subscribe(this, Observer {
                if (viewModel.selectedItemCount == 0) actionMode?.finish()
            }, ItemToggleEvent::class)
        }

        viewModel.liveBus.let {
            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        CONFIRM_DIALOG_CREATE_NEW_FOLDER -> viewModel.addFolder(input)
                    }
                }
            }, SimpleInputDialogFragment.ConfirmEvent::class)

            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        CONFIRM_DIALOG_EXIT -> finish()
                    }
                }
            }, SimpleConfirmDialogFragment.ConfirmEvent::class)
        }

        viewModel.isInMultiSelectionMode = false

        detectNewUrl(intent)
    }

    fun onItemSelected(position: Int) {
        if (!viewModel.isInMultiSelectionMode) {
            viewModel.getListItem(position).let {
                when (it) {
                    is BookmarkListItem -> {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(it.url)
                        startActivity(i)
                    }
                }
            }
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
            when (itemId) {
                R.id.item_new_folder -> {
                    SimpleInputDialogFragment.newInstance(CONFIRM_DIALOG_CREATE_NEW_FOLDER,
                            hint = getString(R.string.hint_folder_name),
                            title = getString(R.string.title_new_folder))
                            .show(supportFragmentManager, TAG_CREATE_NEW_FOLDER)
                }
                R.id.item_sign_in -> {
                    launchSignIn()
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
                val intent = Intent(this, FolderSelectionActivity::class.java)
                intent.putFolderId(viewModel.currentFolderId)
                intent.putFilteredFolderIds(viewModel.getSelectedFolderIds().toIntArray())
                startActivityForResult(intent, REQUEST_ID_MOVE_ITEMS)
            }
            else -> false
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ID_MOVE_ITEMS -> {
                actionMode?.finish()

                if (resultCode != Activity.RESULT_OK) {
                    return
                }

                data?.let {
                    val folderId = data.getFolderId()
                    viewModel.moveSelectedItems(folderId)
                    viewModel.loadFolder(folderId)
                }
            }
            REQUEST_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)
                when (requestCode) {
                    Activity.RESULT_OK -> {
                        // Successfully signed in
//                        FirebaseUserData.value = FirebaseAuth.getInstance().currentUser
                    }
                    else -> {
                        Log.e(TAG, response?.error.toString())
                    }
                }
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_main_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        viewModel.isInMultiSelectionMode = true
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        viewModel.isInMultiSelectionMode = false
    }

    override fun onBackPressed() {
        if (viewModel.currentFolderId == null) {
            SimpleConfirmDialogFragment.newInstance(CONFIRM_DIALOG_EXIT,
                    title = getString(R.string.confirm_exit))
                    .show(supportFragmentManager, TAG_EXIT)
        } else {
            viewModel.loadParentFolder()
        }
    }

    private fun launchSignIn() {
        // Choose authentication providers
        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
//                AuthUI.IdpConfig.FacebookBuilder().build(),
//                AuthUI.IdpConfig.TwitterBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                REQUEST_SIGN_IN)
    }

    private fun detectNewUrl(intent: Intent?) {
        val newUrl = intent?.getSharedUrl()
        newUrl?.let { viewModel.addBookmark(newUrl) }
    }

    private class ViewModelFactory(val viewModelComponent: BookBagAppComponent) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModelComponent.mainComponent().let { component ->
                component.provideMainViewModel().apply { mainComponent = component } as T
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val CONFIRM_DIALOG_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.CONFIRM_DIALOG_CREATE_NEW_FOLDER"
        private const val CONFIRM_DIALOG_EXIT = "github.io.mssjsg.bookbag.main.CONFIRM_DIALOG_EXIT"
        private const val TAG_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.TAG_CREATE_NEW_FOLDER"
        private const val TAG_EXIT = "github.io.mssjsg.bookbag.main.TAG_EXIT"
        private const val REQUEST_ID_MOVE_ITEMS = 1000
        private const val REQUEST_SIGN_IN = 1001
    }
}
