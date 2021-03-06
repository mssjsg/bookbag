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
import android.support.v4.app.Fragment
import android.view.*
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentFolderviewBinding
import github.io.mssjsg.bookbag.folderselection.FolderSelectionFragment
import github.io.mssjsg.bookbag.folderselection.event.FolderSelectionEvent
import github.io.mssjsg.bookbag.folderview.FolderViewViewModel.PageState.*
import github.io.mssjsg.bookbag.intro.IntroFragment
import github.io.mssjsg.bookbag.list.ItemListContainerFragment
import github.io.mssjsg.bookbag.util.extension.observeNonNull
import github.io.mssjsg.bookbag.util.extension.putFolderId
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.widget.SimpleConfirmDialogFragment
import github.io.mssjsg.bookbag.widget.SimpleInputDialogFragment
import javax.inject.Inject


class FolderViewFragment : ItemListContainerFragment<FolderViewViewModel>(),
        ActionMode.Callback, FolderViewViewModel.WebPageViewer, SimpleInputDialogFragment.Listener,
        SimpleConfirmDialogFragment.Listener{
    private var actionMode: ActionMode? = null
    private var clipboardNoticeSnackbar: Snackbar? = null
    private var exitNoticeSnackbar: Snackbar? = null
    private lateinit var folderViewBinding: FragmentFolderviewBinding

    private lateinit var clipboard: ClipboardManager

    @Inject
    lateinit var liveBus: LiveBus

    override fun onViewModelCreated(viewModel: FolderViewViewModel) {
        super.onViewModelCreated(viewModel)
        viewModel.folderViewComponent.inject(this)
        observeViewModel()
        observeDialogEvents()
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        if (childFragment is SimpleConfirmDialogFragment) {
            childFragment.listener = this
        } else if (childFragment is SimpleInputDialogFragment) {
            childFragment.listener = this
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        folderViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_folderview, container, false)
        return folderViewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
                    R.id.item_sign_in_out -> {
                        viewModel.onSignInOutButtonClick();true
                    }
                    else -> false
                }
            })
        }

        viewModel.signInOutText.observeNonNull(this, { textType ->
            folderViewBinding.layoutToolbar.toolbar.menu.findItem(R.id.item_sign_in_out).title = when(textType) {
                FolderViewViewModel.SignInOutText.SIGN_IN -> getString(R.string.menu_item_sign_in)
                FolderViewViewModel.SignInOutText.SIGN_OUT -> getString(R.string.menu_item_sign_out)
            }
        })
    }

    private fun observeViewModel() {
        viewModel.isShowingPasteFromClipboardNotice.observeNonNull(this, {
            if (it) {
                clipboardNoticeSnackbar = Snackbar.make(folderViewBinding.root, R.string.confirm_plaste_clipboard,
                        Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.dialog_ok, {
                        viewModel.onConfirmAddBookmarkFromClipboard()
                    })
                }.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
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
                CONFIRM_SIGN_OUT -> {
                    if (childFragmentManager.findFragmentByTag(TAG_CONFIRM_DIALOG) == null) {
                        SimpleConfirmDialogFragment.newInstance(CONFIRM_DIALOG_SIGN_OUT,
                                getString(R.string.confirm_sign_out))
                                .show(childFragmentManager, TAG_CONFIRM_DIALOG)
                    }
                }
                CONFIRM_DELETE -> {
                    if (childFragmentManager.findFragmentByTag(TAG_CONFIRM_DIALOG) == null) {
                        SimpleConfirmDialogFragment.newInstance(CONFIRM_DIALOG_DELETE_ITEMS,
                                getString(R.string.confirm_delete_items))
                                .show(childFragmentManager, TAG_CONFIRM_DIALOG)
                    }
                }
                MOVING_ITEMS -> {
                    navigationManager?.let { navigationManager ->
                        if (navigationManager.isFragmentAdded(TAG_CONFIRM_DIALOG)) {
                            return@observeNonNull
                        }

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
                        navigationManager.addToBackStack(fragment, TAG_MOVE, R.anim.pop_enter_animation, R.anim.pop_exit_animation)
                    }
                }
                ADDING_FOLDER -> {
                    if (childFragmentManager.findFragmentByTag(TAG_INPUT_DIALOG) == null) {
                        SimpleInputDialogFragment.newInstance(CONFIRM_DIALOG_CREATE_NEW_FOLDER,
                                hint = getString(R.string.hint_folder_name),
                                title = getString(R.string.title_new_folder))
                                .show(childFragmentManager, TAG_INPUT_DIALOG)
                    }

                    true
                }
                BROWSE -> {
                    childFragmentManager?.apply {
                        arrayOf(TAG_CONFIRM_DIALOG, TAG_MOVE, TAG_INPUT_DIALOG).forEach {
                            val fragment = findFragmentByTag(it)
                            fragment?.apply {
                                beginTransaction().remove(this).commit()
                            }
                        }
                    }
                }
                APP_FINISHED -> {
                    activity?.finish()
                }
                VIEW_FINISHED -> {
                    navigationManager?.setCurrentFragment(IntroFragment.newInstance())
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
    }

    private fun observeDialogEvents() {
        liveBus.apply {
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

    override fun onConfirm(simpleInputDialogFragment: SimpleInputDialogFragment, requestId: String, input: String) {
        when (requestId) {
            CONFIRM_DIALOG_CREATE_NEW_FOLDER -> viewModel.onConfirmNewFolderName(input)
        }
    }

    override fun onCancel(simpleInputDialogFragment: SimpleInputDialogFragment, requestId: String) {
        when (requestId) {
            CONFIRM_DIALOG_CREATE_NEW_FOLDER -> viewModel.onCancelNewFolder()
        }
    }

    override fun onConfirm(simpleConfirmDialogFragment: SimpleConfirmDialogFragment, requestId: String) {
        when (requestId) {
            CONFIRM_DIALOG_DELETE_ITEMS -> viewModel.onConfirmDeleteItems()
            CONFIRM_DIALOG_SIGN_OUT -> viewModel.onConfirmSignOut()
        }
    }

    override fun onCancel(simpleConfirmDialogFragment: SimpleConfirmDialogFragment, requestId: String) {
        when (requestId) {
            CONFIRM_DIALOG_DELETE_ITEMS -> viewModel.onCancelDeleteItems()
            CONFIRM_DIALOG_SIGN_OUT -> viewModel.onCancelSignOut()
        }
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
        private const val CONFIRM_DIALOG_SIGN_OUT = "github.io.mssjsg.bookbag.main.CONFIRM_DIALOG_SIGN_OUT"
        private const val TAG_INPUT_DIALOG = "github.io.mssjsg.bookbag.main.TAG_INPUT_DIALOG"
        private const val TAG_MOVE = "github.io.mssjsg.bookbag.main.TAG_MOVE"
        private const val TAG_CONFIRM_DIALOG = "github.io.mssjsg.bookbag.main.TAG_CONFIRM_DIALOG"
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