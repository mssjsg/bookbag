package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.Observer
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
import github.io.mssjsg.bookbag.list.event.ItemClickEvent
import github.io.mssjsg.bookbag.list.event.ItemLongClickEvent
import github.io.mssjsg.bookbag.list.event.ItemToggleEvent
import github.io.mssjsg.bookbag.list.listitem.FolderListItem

class ItemListFragment : Fragment() {

    companion object {
        fun newInstance(): ItemListFragment {
            val itemListFragment = ItemListFragment()
            return itemListFragment
        }
    }

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
        if (context is ItemListViewModelProvider) {
            itemListViewModelProvider = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //init view model
        viewModel = itemListViewModelProvider.getItemListViewModel()
        viewModel.loadCurrentFolder()
        viewModel.localLiveBus.let {
            //init event
            it.subscribe(this, Observer {
                viewModel.setSelected(it!!.position, true)
            }, ItemLongClickEvent::class)

            it.subscribe(this, Observer {
                it?.let { onItemSelected(it.position) }
            }, ItemClickEvent::class)
        }

        //init adapter
        mainListAdapter = MainListAdapter(viewModel)
        pathListAdapter = PathListAdapter(viewModel)

        //init bind views
        listBinding.viewmodel = viewModel
        listBinding.bookmarksList.adapter = mainListAdapter
        listBinding.bookmarksList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        listBinding.pathsList.adapter = pathListAdapter
    }

    fun onItemSelected(position: Int) {
        if (viewModel.isInMultiSelectionMode) {
            viewModel.toggleSelected(position)
            viewModel.localLiveBus.post(ItemToggleEvent(position))
        } else {
            viewModel.getListItem(position).let {
                when (it) {
                    is FolderListItem -> {
                        viewModel.loadFolder(it.folderId)
                    }
                }
            }
        }
    }
}