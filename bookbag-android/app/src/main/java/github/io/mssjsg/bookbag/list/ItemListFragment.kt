package github.io.mssjsg.bookbag.list

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.FragmentListBinding
import github.io.mssjsg.bookbag.list.listitem.BookmarkListItem
import github.io.mssjsg.bookbag.list.listitem.FolderListItem
import github.io.mssjsg.bookbag.util.getSharedUrl

class ItemListFragment : Fragment() {

    companion object {
        fun newInstance(): ItemListFragment {
            val itemListFragment = ItemListFragment()
            return itemListFragment
        }
    }

    protected lateinit var mViewModel: ItemListViewModel
    private lateinit var listBinding: FragmentListBinding
    private lateinit var mainListAdapter: MainListAdapter
    private lateinit var pathListAdapter: PathListAdapter
    private lateinit var itemListViewModelProvider: ItemListViewModelProvider

    var currentFolderId: Int? = null
        private set
        get() = mViewModel.currentFolderId

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
        mViewModel = itemListViewModelProvider.getItemListViewModel()

        onViewModelCreated(mViewModel)

        mViewModel.loadFolder(mViewModel.currentFolderId)
        mViewModel.localLiveBus.let {
            //init event
            it.subscribe(this, Observer {
                mViewModel.setSelected(it!!.position, true)
            }, ItemLongClickEvent::class)

            it.subscribe(this, Observer {
                it?.let { onItemSelected(it.position) }
            }, ItemClickEvent::class)
        }

        //init adapter
        mainListAdapter = MainListAdapter(mViewModel)
        pathListAdapter = PathListAdapter(mViewModel)

        //init bind views
        listBinding.viewmodel = mViewModel
        listBinding.bookmarksList.adapter = mainListAdapter
        listBinding.bookmarksList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        listBinding.pathsList.adapter = pathListAdapter
    }

    protected open fun onViewModelCreated(viewModel: ItemListViewModel) {
        //do nothing
    }

    fun onItemSelected(position: Int) {
        if (mViewModel.isInActionMode) {
            mViewModel.toggleSelected(position)
        } else {
            mViewModel.getListItem(position).let {
                when (it) {
                    is BookmarkListItem -> {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(it.url)
                        startActivity(i)
                    }
                    is FolderListItem -> {
                        mViewModel.loadFolder(it.folderId)
                    }
                }
            }
        }
    }
}