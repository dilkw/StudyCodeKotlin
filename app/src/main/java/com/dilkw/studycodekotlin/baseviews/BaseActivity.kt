package com.dilkw.studycodekotlin.baseviews

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

open class BaseActivity: AppCompatActivity() {

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        return if (handleFragmentOnBackPress()) {
//            true
//        }else {
//            super.onKeyDown(keyCode, event)
//        }
//    }
    private fun handleFragmentOnBackPressed(): Boolean {
        var primaryNavigationFragment = supportFragmentManager.primaryNavigationFragment
        if (primaryNavigationFragment is NavHostFragment) {
            while (primaryNavigationFragment is NavHostFragment) {
                primaryNavigationFragment = (primaryNavigationFragment as NavHostFragment).childFragmentManager.primaryNavigationFragment
            }
        }
        // 当前活动的 fragment 为 BaseFragment 子类时，并且primaryNavigationFragment已经处理返回时间
        return primaryNavigationFragment is BaseFragment && (primaryNavigationFragment as BaseFragment).onFragmentBackPressed()
    }

    override fun onBackPressed() {
        if (!handleFragmentOnBackPressed()) {
            super.onBackPressed()
        }
    }

}