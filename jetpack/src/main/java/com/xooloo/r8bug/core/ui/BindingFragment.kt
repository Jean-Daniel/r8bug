package com.xooloo.r8bug.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.xooloo.r8bug.core.ext.inflate
import com.xooloo.r8bug.core.ext.parameterType

open class BindingFragment<VB : ViewBinding> : Fragment() {

    // Can't use lateinit as we must be able to reset it
    private var _layout: VB? = null

    protected val binding: VB
        get() {
            if (_layout != null)
                return _layout!!
            throw IllegalStateException("Layout must be initialized before use")
        }

    // to workaround some edge cases
    val isBindingAvailable: Boolean
        get() = _layout != null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _layout = onCreateViewBinding(inflater, container)
        return binding.root
    }

    @CallSuper
    override fun onDestroyView() {
        _layout = null
        super.onDestroyView()
    }
}


private fun <VB : ViewBinding> Fragment.onCreateViewBinding(
    inflater: LayoutInflater,
    container: ViewGroup?
): VB {
    return inflater.inflate(javaClass.parameterType(), container, false)
}

