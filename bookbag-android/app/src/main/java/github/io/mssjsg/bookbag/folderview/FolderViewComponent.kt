package github.io.mssjsg.bookbag.folderview

import dagger.Subcomponent
import github.io.mssjsg.bookbag.ViewModelScope

@Subcomponent()
@ViewModelScope
interface FolderViewComponent {
    fun provideFolderViewModel(): FolderViewViewModel
}