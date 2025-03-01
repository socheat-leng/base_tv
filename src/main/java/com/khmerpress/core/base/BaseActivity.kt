package com.khmerpress.core.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout

abstract class BaseActivity : AppCompatActivity() {

    // Objects
    private val TAG: String = this::class.java.simpleName
    protected lateinit var mActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
    }

    protected fun setupEdgeToEdge() {
        val window: Window = window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false

        val rootView = getRootView()
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                applyInsets(v, insets)
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    protected abstract fun getRootView(): View?
    protected abstract fun getAppBarLayout(): Toolbar?

    protected fun applyInsets(view: View, insets: WindowInsetsCompat) {
        val inset: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            leftMargin = inset.left
            bottomMargin = inset.bottom
            rightMargin = inset.right
            view.layoutParams = this
        }

        val toolbar = getAppBarLayout()
        when (val param = toolbar?.layoutParams) {
            is AppBarLayout.LayoutParams -> {
                param.topMargin = inset.top
                toolbar.layoutParams = param
            }

            is RelativeLayout.LayoutParams -> {
                param.topMargin = inset.top
                toolbar.layoutParams = param
            }
        }
    }
}