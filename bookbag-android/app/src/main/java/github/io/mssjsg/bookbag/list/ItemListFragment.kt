package github.io.mssjsg.bookbag.list

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentListBinding

class ItemListFragment : Fragment() {

    protected lateinit var viewModel: ItemListViewModel
    private lateinit var listBinding: FragmentListBinding
    private lateinit var mainListAdapter: MainListAdapter
    private lateinit var pathListAdapter: PathListAdapter
    private lateinit var itemListViewModelProvider: ItemListViewModelProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        listBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        return listBinding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (parentFragment is ItemListViewModelProvider) {
            itemListViewModelProvider = parentFragment as ItemListViewModelProvider
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //init view model
        viewModel = itemListViewModelProvider.getItemListViewModel()
        viewModel.onViewLoaded(getFolderId(this))

        //init adapter
        mainListAdapter = MainListAdapter(viewModel)
        pathListAdapter = PathListAdapter(viewModel)

        //init bind views
        listBinding.viewmodel = viewModel
        listBinding.bookmarksList.adapter = mainListAdapter
        listBinding.bookmarksList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        listBinding.pathsList.adapter = pathListAdapter
    }

    companion object {
        const val ARG_FOLDER_ID = "ARG_FOLDER_ID"

        fun newInstance(folderId: String?): ItemListFragment {
            val itemListFragment = ItemListFragment()
            val arguments = Bundle().apply {
                putString(ARG_FOLDER_ID, folderId)
            }
            itemListFragment.arguments = arguments
            return itemListFragment
        }

        fun getFolderId(itemListFragment: ItemListFragment): String? {
            return itemListFragment.arguments?.getString(ARG_FOLDER_ID)
        }
    }
}