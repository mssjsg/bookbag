package github.io.mssjsg.bookbag.folderview

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
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
import github.io.mssjsg.bookbag.util.extension.observeNonNull
import github.io.mssjsg.bookbag.util.extension.observeNullable
import github.io.mssjsg.bookbag.util.extension.putFolderId
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.widget.SimpleConfirmDialogFragment
import github.io.mssjsg.bookbag.widget.SimpleInputDialogFragment
import javax.inject.Inject


class FolderViewFragment : ItemListContainerFragment<FolderViewViewModel>(), ActionMode.Callback,
        FolderViewViewModel.WebPageViewer {

    private var actionMode: ActionMode? = null
    private var clipboardNoticeSnackbar: Snackbar? = null
    private var exitNoticeSnackbar: Snackbar? = null
    private lateinit var folderViewBinding: FragmentFolderviewBinding

    private lateinit var clipboard: ClipboardManager

    @Inject
    lateinit var liveBus: LiveBus

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        folderViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_folderview, container, false)
        return folderViewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.folderViewComponent.inject(this)
        clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        getSharedUrl(this)?.let {
            viewModel.onTextShared(it)
            clearSharedUrl(this)
        }

        folderViewBinding.layoutToolbar.toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener({
                when (it.itemId) {
                    R.id.item_new_folder -> {
                        viewModel.onNewFolderButtonClick();true
                    }
                    R.id.item_sign_out -> {
                        viewModel.onSignOutButtonClick();true
                    }
                    else -> false
                }
            })
        }

        observeViewModel()
        observeDialogEvents()
    }

    private fun observeViewModel() {
        viewModel.isShowingPasteFromClipboardNotice.observeNonNull(this, {
            if (it) {
                clipboardNoticeSnackbar = Snackbar.make(folderViewBinding.root, R.string.confirm_plaste_clipboard,
                        Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.dialog_ok, {
                        viewModel.onConfirmAddBookmarkFromClipboard()
                    })
                }.addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        clipboardNoticeSnackbar = null
                        viewModel.onPasteClipboardNoticeDismissed()
                    }
                })
                clipboardNoticeSnackbar?.show()
            } else {
                clipboardNoticeSnackbar?.let {
                    it.dismiss()
                    clipboardNoticeSnackbar = null
                }
            }
        })

        viewModel.isInMultiSelectionMode.observeNonNull(this, {
            if (it) {
                actionMode = folderViewBinding.layoutToolbar.toolbar.startActionMode(this@FolderViewFragment)
            } else {
                actionMode?.finish()
                actionMode = null
            }
        })

        viewModel.pageState.observeNonNull(this, {
            when (it) {
                FolderViewViewModel.PageState.DELETING_ITEMS -> {
                    SimpleConfirmDialogFragment.newInstance(CONFIRM_DIALOG_DELETE_ITEMS,
                            getString(R.string.confirm_delete_items))
                            .show(childFragmentManager, TAG_CONFIRM_DELETE)
                }
                FolderViewViewModel.PageState.MOVING_ITEMS -> {
                    val selectedCount = viewModel.selectedItemCount
                    val title = when (selectedCount) {
                        1 -> R.string.title_move_to_one
                        else -> R.string.title_move_to_other
                    }.let { getString(it, selectedCount) }
                    val fragment = FolderSelectionFragment.newInstance(
                            REQUEST_ID_MOVE_ITEMS,
                            viewModel.currentFolderId,
                            viewModel.getSelectedFolderIds().toTypedArray(),
                            title,
                            getString(R.string.btn_confirm_move)
                    )
                    navigationManager?.addToBackStack(fragment, TAG_CREATE_MOVE, R.anim.pop_enter_animation, R.anim.pop_exit_animation)
                }
                FolderViewViewModel.PageState.ADDING_FOLDER -> {
                    SimpleInputDialogFragment.newInstance(CONFIRM_DIALOG_CREATE_NEW_FOLDER,
                            hint = getString(R.string.hint_folder_name),
                            title = getString(R.string.title_new_folder))
                            .show(childFragmentManager, TAG_CREATE_NEW_FOLDER)

                    true
                }
                FolderViewViewModel.PageState.BROWSE -> {
                    while (childFragmentManager.backStackEntryCount > 0) {
                        childFragmentManager.popBackStackImmediate()
                    }
                }
                FolderViewViewModel.PageState.FINISHED -> {
                    activity?.finish()
                }
            }
        })

        viewModel.isShowingExitConfirmNotice.observeNonNull(this, {
            if (it) {
                exitNoticeSnackbar = Snackbar.make(folderViewBinding.root, R.string.confirm_exit, Snackbar.LENGTH_SHORT).apply {
                    setAction(R.string.dialog_cancel, {
                        viewModel.onCancelExitNotice()
                    })

                    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            viewModel.onExitNoticeDismissed()
                        }
                    })
                }

                exitNoticeSnackbar?.show()
            } else {
                exitNoticeSnackbar?.dismiss()
                exitNoticeSnackbar = null
            }
        })

        viewModel.bookbagUserData.observeNullable(this, {
            if (it == null) {
                navigationManager?.setCurrentFragment(IntroFragment.newInstance())
            }
        })
    }

    private fun observeDialogEvents() {
        liveBus.apply {
            subscribe(this@FolderViewFragment, {
                when (it.requestId) {
                    FolderViewFragment.CONFIRM_DIALOG_CREATE_NEW_FOLDER -> viewModel.onConfirmNewFolderName(it.input)
                }
            }, SimpleInputDialogFragment.ConfirmEvent::class)

            subscribe(this@FolderViewFragment, {
                when (it.requestId) {
                    FolderViewFragment.CONFIRM_DIALOG_DELETE_ITEMS -> viewModel.onCancelDeleteItems()
                }
            }, SimpleConfirmDialogFragment.CancelEvent::class)

            subscribe(this@FolderViewFragment, {
                when (it.requestId) {
                    FolderViewFragment.CONFIRM_DIALOG_DELETE_ITEMS -> viewModel.onConfirmDeleteItems()
                }
            }, SimpleConfirmDialogFragment.ConfirmEvent::class)

            subscribe(this@FolderViewFragment, {
                when (it.requestId) {
                    FolderViewFragment.REQUEST_ID_MOVE_ITEMS -> {
                        if (it.confirmed) {
                            viewModel.onConfirmFolderSelection(it.folderId)
                        } else {
                            viewModel.onCancelFolderSelection()
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
                viewModel.onPasteClipboardText(text.toString())
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
                viewModel.onDeleteItemsButtonClick()
                true
            }
            R.id.item_move -> {
                viewModel.onMoveItemsButtonClick()
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
        return viewModel.onBackPressed()
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
        private const val TAG_CONFIRM_DELETE = "github.io.mssjsg.bookbag.main.TAG_CONFIRM_DELETE"
        private const val REQUEST_ID_MOVE_ITEMS = 1000
        private const val ARG_NEW_SHARED_URL = "github.io.mssjsg.bookbag.main.ARG_NEW_SHARED_URL"

        fun newInstance(folderId: String? = null, sharedUrl: String? = null): FolderViewFragment {
            val fragment = FolderViewFragment()
            fragment.arguments = Bundle().apply {
                putFolderId(folderId)
                putString(ARG_NEW_SHARED_URL, sharedUrl)
            }
            return fragment
        }

        fun getSharedUrl(fragment: FolderViewFragment): String? {
            return fragment.arguments?.getString(ARG_NEW_SHARED_URL)
        }

        fun clearSharedUrl(fragment: FolderViewFragment) {
            fragment.arguments?.remove(ARG_NEW_SHARED_URL)
        }
    }

    override fun isSignInRequired(): Boolean {
        return true
    }
}