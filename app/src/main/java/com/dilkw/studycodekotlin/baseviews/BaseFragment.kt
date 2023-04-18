package com.dilkw.studycodekotlin.baseviews

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    open fun onFragmentBackPressed(): Boolean {
        return false
    }

}