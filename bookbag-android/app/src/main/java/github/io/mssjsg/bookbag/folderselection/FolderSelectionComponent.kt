package github.io.mssjsg.bookbag.folderselection

import dagger.Subcomponent
import github.io.mssjsg.bookbag.ViewModelScope

@Subcomponent()
@ViewModelScope
interface FolderSelectionComponent {
    fun provideFolderSelectionViewModel(): FolderSelectionViewModel
}