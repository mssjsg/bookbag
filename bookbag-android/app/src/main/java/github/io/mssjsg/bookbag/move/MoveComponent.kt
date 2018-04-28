package github.io.mssjsg.bookbag.move

import dagger.Subcomponent
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope

@Subcomponent()
@ViewModelScope
interface MoveComponent {
    fun inject(moveActivity: MoveActivity)
}