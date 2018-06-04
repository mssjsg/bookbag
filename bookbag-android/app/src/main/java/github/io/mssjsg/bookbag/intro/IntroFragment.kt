package github.io.mssjsg.bookbag.intro

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.BookbagFragment
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.databinding.FragmentIntroBinding
import github.io.mssjsg.bookbag.folderview.FolderViewFragment
import github.io.mssjsg.bookbag.user.GoogleAuthHelper

class IntroFragment: BookbagFragment() {

    private lateinit var fragmentIntroBinding: FragmentIntroBinding

    private lateinit var introViewModel: IntroViewModel
    private lateinit var googleAuthHelper: GoogleAuthHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentIntroBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        return fragmentIntroBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentIntroBinding.setLifecycleOwner(this)
        fragmentIntroBinding.btnSigninGoogle.setOnClickListener({
            introViewModel.signIn()
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        introViewModel = ViewModelProviders.of(this, IntroFragment.ViewModelFactory(getAppComponent()))
                .get(IntroViewModel::class.java)

        introViewModel.userData.observe(this, Observer {
            it?.let {
                navigationManager?.setCurrentFragment(FolderViewFragment.newInstance())
            }
        })

        googleAuthHelper = introViewModel.googleAuthHelper
        googleAuthHelper.fragment = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleAuthHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun isSignInRequired(): Boolean {
        return false
    }

    private class ViewModelFactory(val viewModelComponent: BookBagAppComponent) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModelComponent.introComponent().let { component ->
                component.provideViewModel() as T
            }
        }
    }

    companion object {
        fun newInstance(): IntroFragment {
            return IntroFragment()
        }
    }
}