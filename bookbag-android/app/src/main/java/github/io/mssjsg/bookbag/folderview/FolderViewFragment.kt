package github.io.mssjsg.bookbag.folderview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.view.*
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentFolderviewBinding
import github.io.mssjsg.bookbag.folderselection.FolderSelectionFragment
import github.io.mssjsg.bookbag.folderselection.event.FolderSelectionEvent
import github.io.mssjsg.bookbag.intro.IntroFragment
import github.io.mssjsg.bookbag.list.ItemListContainerFragment
import github.io.mssjsg.bookbag.util.extension.putFolderId
import github.io.mssjsg.bookbag.util.linkpreview.SearchUrls
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.widget.SimpleConfirmDialogFragment
import github.io.mssjsg.bookbag.widget.SimpleInputDialogFragment
import javax.inject.Inject


class FolderViewFragment : ItemListContainerFragment<FolderViewViewModel>(), ActionMode.Callback,
        FolderViewViewModel.WebPageViewer {

    private var actionMode: ActionMode? = null
    private lateinit var folderViewBinding: FragmentFolderviewBinding

    private var pendingNewBookmarkUrl: String? = null
    private var isShowingExitSnackbar: Boolean = false

    private lateinit var clipboard: ClipboardManager

    @Inject
    lateinit var liveBus: LiveBus

    fun addBookmark(url: String) {
        try {
            viewModel.addBookmark(url)
        } catch (e: UninitializedPropertyAccessException) {
            pendingNewBookmarkUrl = url
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        folderViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_folderview, container, false)
        return folderViewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.folderViewComponent.inject(this)
        clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        pendingNewBookmarkUrl?.let { viewModel.addBookmark(it) }
        pendingNewBookmarkUrl = null

        folderViewBinding.layoutToolbar.toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener({
                when (it.itemId) {
                    R.id.item_new_folder -> {
                        SimpleInputDialogFragment.newInstance(CONFIRM_DIALOG_CREATE_NEW_FOLDER,
                                hint = getString(R.string.hint_folder_name),
                                title = getString(R.string.title_new_folder))
                                .show(childFragmentManager, TAG_CREATE_NEW_FOLDER)

                        true
                    }
                    R.id.item_sign_out -> {
                        viewModel.signOut()
                        true
                    }
                    else -> false
                }
            })
        }

        viewModel.isInMultiSelectionMode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.isInMultiSelectionMode.get()) {
                    actionMode = folderViewBinding.layoutToolbar.toolbar.startActionMode(this@FolderViewFragment)
                } else {
                    actionMode?.finish()
                    actionMode = null
                }
            }
        })
        viewModel.bookbagUserData.observe(this, Observer {
            if (it == null) {
                navigationManager?.setCurrentFragment(IntroFragment.newInstance())
            }
        })

        observeDialogEvents()
    }

    private fun observeDialogEvents() {
        liveBus.let {
            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        FolderViewFragment.CONFIRM_DIALOG_CREATE_NEW_FOLDER -> viewModel.addFolder(input)
                    }
                }
            }, SimpleInputDialogFragment.ConfirmEvent::class)

            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        FolderViewFragment.CONFIRM_DIALOG_DELETE_ITEMS -> actionMode?.finish()
                    }
                }
            }, SimpleConfirmDialogFragment.CancelEvent::class)

            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        FolderViewFragment.CONFIRM_DIALOG_DELETE_ITEMS -> {
                            viewModel.deleteSelectedItems()
                            actionMode?.finish()
                        }
                    }
                }
            }, SimpleConfirmDialogFragment.ConfirmEvent::class)

            it.subscribe(this, Observer {
                it?.apply {
                    when (requestId) {
                        FolderViewFragment.REQUEST_ID_MOVE_ITEMS -> {
                            viewModel.onFolderSelected(confirmed, folderId)
                        }
                    }
                }
            }, FolderSelectionEvent::class)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.webPageViewer = this
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.webPageViewer = null
    }

    override fun onResume() {
        super.onResume()
        handleClipboard()
    }

    private fun handleClipboard() {
        if (clipboard.hasPrimaryClip()) {
            clipboard.primaryClip.getItemAt(0).text?.let { text ->
                val urls = SearchUrls.matches(text.toString())
                if (urls.size > 0) {
                    Snackbar.make(folderViewBinding.root, R.string.confirm_plaste_clipboard,
                            Snackbar.LENGTH_LONG).apply {
                        setAction(R.string.dialog_ok, {
                            viewModel.addBookmark(text.toString())
                            dismiss()
                        })
                    }.show()
                }
            }
        }
    }

    override fun onCreateViewModel(): FolderViewViewModel {
        return ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(FolderViewViewModel::class.java)
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                SimpleConfirmDialogFragment.newInstance(CONFIRM_DIALOG_DELETE_ITEMS,
                        getString(R.string.confirm_delete_items)).show(fragmentManager,
                        CONFIRM_DIALOG_DELETE_ITEMS)
                true
            }
            R.id.item_move -> {
                val selectedCount = viewModel.selectedItemCount
                val title = when (selectedCount) {
                    1 -> R.string.title_move_to_one
                    else -> R.string.title_move_to_other
                }.let { getString(it, selectedCount) }
                viewModel.cacheSelectedItems()
                actionMode?.finish()
                val fragment = FolderSelectionFragment.newInstance(
                        REQUEST_ID_MOVE_ITEMS,
                        viewModel.currentFolderId,
                        viewModel.getSelectedFolderIds().toTypedArray(),
                        title,
                        getString(R.string.btn_confirm_move)
                )
                navigationManager?.addToBackStack(fragment, TAG_CREATE_MOVE, R.anim.pop_enter_animation, R.anim.pop_exit_animation)
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
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        viewModel.onSelectionModeDismissed()
    }

    override fun onBackPressed(): Boolean {
        if (viewModel.currentFolderId != null) {
            return super.onBackPressed()
        }

        if (isShowingExitSnackbar) {
            activity?.finish()
            return true
        }

        Snackbar.make(folderViewBinding.root, R.string.confirm_exit, Snackbar.LENGTH_SHORT).apply {
            setAction(R.string.dialog_cancel, {
                dismiss()
            })

            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    isShowingExitSnackbar = true
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    isShowingExitSnackbar = false
                }
            })
        }.show()

        return true
    }

    override fun showPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private class ViewModelFactory(val viewModelComponent: BookBagAppComponent) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModelComponent.folderViewComponent().let { component ->
                component.provideFolderViewModel().apply { folderViewComponent = component } as T
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val CONFIRM_DIALOG_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.CONFIRM_DIALOG_CREATE_NEW_FOLDER"
        private const val CONFIRM_DIALOG_DELETE_ITEMS = "github.io.mssjsg.bookbag.main.CONFIRM_DIALOG_DELETE_ITEMS"
        private const val TAG_CREATE_NEW_FOLDER = "github.io.mssjsg.bookbag.main.TAG_CREATE_NEW_FOLDER"
        private const val TAG_CREATE_MOVE = "github.io.mssjsg.bookbag.main.TAG_MOVE"
        private const val REQUEST_ID_MOVE_ITEMS = 1000

        fun newInstance(folderId: String? = null): FolderViewFragment {
            val fragment = FolderViewFragment()
            fragment.arguments = Bundle().apply {
                putFolderId(folderId)
            }
            return fragment
        }
    }

    override fun isSignInRequired(): Boolean {
        return true
    }
}