@file:Suppress("NOTHING_TO_INLINE")

package com.xooloo.r8bug.core.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

fun <T> Flow<T>.collect(
    lifecycleOwner: LifecycleOwner,
    action: suspend CoroutineScope.(value: T) -> Unit
): Job =
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        // Using collectLatest as this is a sensible default for UI operations
        collectLatest { action(it) }
    }
