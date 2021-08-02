package com.xooloo.r8bug.core.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

private const val CONTENT_ID: Int = 0x45963254

abstract class SimpleFragmentActivity<F : Fragment> : AppCompatActivity() {

    @set:JvmName("_setContentView")
    lateinit var contentView: ViewGroup
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using frame layout (instead of FragmentContainerView) so we can still insert other view if needed (animations)
        contentView = FrameLayout(this).apply { id = CONTENT_ID }
        setContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val frag = supportFragmentManager.findFragmentById(CONTENT_ID)
        if (frag == null) {
            supportFragmentManager.beginTransaction().add(CONTENT_ID, onCreateFragment()).commit()
        }
    }

    @Suppress("UNCHECKED_CAST")
    val fragment: F?
        get() = supportFragmentManager.findFragmentById(CONTENT_ID) as F?

    abstract fun onCreateFragment(): F
}
