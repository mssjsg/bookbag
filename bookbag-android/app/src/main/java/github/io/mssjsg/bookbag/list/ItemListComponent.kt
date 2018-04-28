package github.io.mssjsg.bookbag.list

import dagger.Subcomponent
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope

@Subcomponent()
@ViewModelScope
interface ItemListComponent {
    fun inject(itemListFragment: ItemListFragment)

    fun provideItemListViewModel(): ItemListViewModel
}