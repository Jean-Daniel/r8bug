@file:Suppress("NOTHING_TO_INLINE") // Aliases to public API.

package com.xooloo.r8bug.core.ext

import android.content.Context
import androidx.viewbinding.ViewBinding

// ------ View Binding helpers
inline val ViewBinding.context: Context
    get() = root.context
