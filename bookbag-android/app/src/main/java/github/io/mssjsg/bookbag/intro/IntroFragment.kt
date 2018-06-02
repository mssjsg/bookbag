package github.io.mssjsg.bookbag.intro

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.io.mssjsg.bookbag.BookbagFragment
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentIntroBinding
import github.io.mssjsg.bookbag.folderview.FolderViewFragment

class IntroFragment: BookbagFragment() {

    private lateinit var fragmentIntroBinding: FragmentIntroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentIntroBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        return fragmentIntroBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentIntroBinding.btnSigninGoogle.setOnClickListener({
            navigationManager?.setCurrentFragment(FolderViewFragment.newInstance())
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun isSignInRequired(): Boolean {
        return false
    }

    companion object {
        fun newInstance(): IntroFragment {
            return IntroFragment()
        }
    }
}