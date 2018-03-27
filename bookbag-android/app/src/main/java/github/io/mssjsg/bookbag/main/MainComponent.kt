package github.io.mssjsg.bookbag.main

import dagger.Subcomponent
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope

/**
 * Created by Sing on 27/3/2018.
 */
@Subcomponent()
@ViewModelScope
interface MainComponent {
    fun inject(mainActivity: MainActivity)

    fun provideMainViewModel(): MainViewModel
}
