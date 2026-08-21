package com.example

import android.os.Build
import android.view.WindowManager

fun test(params: WindowManager.LayoutParams) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        // trying fields
        val max = params.preferredMaxDisplayRefreshRate
        val min = params.preferredMinDisplayRefreshRate
    }
}
