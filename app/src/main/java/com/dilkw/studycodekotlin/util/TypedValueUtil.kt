package com.dilkw.studycodekotlin.util

import android.content.res.Resources
import android.util.TypedValue

class TypedValueUtil {

    companion object {
        @JvmStatic
        fun dp2px(dpValue: Int): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), Resources.getSystem().displayMetrics).toInt()
        }
    }

}