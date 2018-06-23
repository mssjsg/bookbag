package github.io.mssjsg.bookbag.folderselection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentSelectFolderBinding
import github.io.mssjsg.bookbag.list.ItemListContainerFragment
import github.io.mssjsg.bookbag.util.extension.putFilteredFolderIds
import github.io.mssjsg.bookbag.util.extension.putFolderId

class FolderSelectionFragment: ItemListContainerFragment<FolderSelectionViewModel>() {

    private lateinit var mainBinding: FragmentSelectFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.requestId = FolderSelectionFragment.getRequestId(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_folder, container, false)
        return mainBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainBinding.layoutToolbar.toolbar.let {
            it.title = getTitle(this)
            it.inflateMenu(R.menu.menu_folder_selection)
        }

        mainBinding.btnConfirm.text = getConfirmButtonText(this)

        mainBinding.btnConfirm.setOnClickListener({
            viewModel.onConfirmButtonClick()
        })

        mainBinding.btnCanecl.setOnClickListener({
            viewModel.onCancelButtonClick()
        })

        viewModel.isFinished.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.isFinished.get()) {
                    fragmentManager?.popBackStackImmediate()
                }
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return viewModel.onBackPressed()
    }

    override fun isSignInRequired(): Boolean {
        return true
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

        const val ARG_TITLE = "github.io.mssjsg.bookbag.folderselection.ARG_TITLE"
        const val ARG_CONFIRM_BUTTON = "github.io.mssjsg.bookbag.folderselection.ARG_CONFIRM_BUTTON"
        const val ARG_REQUEST_ID = "github.io.mssjsg.bookbag.folderselection.ARG_REQUEST_ID"

        fun newInstance(requestId:Int, folderId: String?, filteredFolderIds: Array<String>,
                        title: String, confirmButton: String): FolderSelectionFragment {
            val bundle = Bundle()
            bundle?.apply {
                putString(ARG_TITLE, title)
                putString(ARG_CONFIRM_BUTTON, confirmButton)
                putInt(ARG_REQUEST_ID, requestId)
                putFolderId(folderId)
                putFilteredFolderIds(filteredFolderIds)
            }

            val folderSelectionFragment = FolderSelectionFragment()
            folderSelectionFragment.arguments = bundle
            return folderSelectionFragment
        }

        private fun getTitle(fragment: FolderSelectionFragment): String {
            return fragment.arguments?.getString(ARG_TITLE)!!
        }

        private fun getConfirmButtonText(fragment: FolderSelectionFragment): String {
            return fragment.arguments?.getString(ARG_CONFIRM_BUTTON)!!
        }

        private fun getRequestId(fragment: FolderSelectionFragment): Int {
            return fragment.arguments?.getInt(ARG_REQUEST_ID)!!
        }
    }
}