package com.xooloo.r8bug.util

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.xooloo.r8bug.core.ui.ViewBindingHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

interface ListEntryFactory {
    val type: Int
    fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, scope: LifecycleCoroutineScope): ScopedViewBindingHolder<*>
}

fun ListEntryFactory.factory(): Pair<Int, (LayoutInflater, ViewGroup, LifecycleCoroutineScope) -> ScopedViewBindingHolder<*>> = type to this::onCreateViewHolder

abstract class ListEntry {

    abstract val type: Int
    abstract val identifier: Any

    abstract fun hasSameContents(other: ListEntry): Boolean

    class CallBack<E : ListEntry> : DiffUtil.ItemCallback<E>() {
        override fun areItemsTheSame(oldItem: E, newItem: E): Boolean {
            if (oldItem.type != newItem.type)
                return false

            return oldItem.identifier == newItem.identifier
        }

        override fun areContentsTheSame(oldItem: E, newItem: E): Boolean {
            return oldItem.hasSameContents(newItem)
        }
    }
}

abstract class ListEntryAdapter<E : ListEntry>(private val scope: LifecycleCoroutineScope) : ListAdapter<E, ScopedViewBindingHolder<*>>(ListEntry.CallBack()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onViewRecycled(holder: ScopedViewBindingHolder<*>) {
        holder.onRecycle()
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScopedViewBindingHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        return onCreateViewHolder(inflater, parent, viewType, scope)
    }

    abstract fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int, scope: LifecycleCoroutineScope): ScopedViewBindingHolder<*>
}

open class ScopedViewBindingHolder<VB : ViewBinding>(binding: VB, private val scope: LifecycleCoroutineScope) : ViewBindingHolder<VB>(binding) {

    private val _jobsDelegate = lazy(LazyThreadSafetyMode.NONE) { ArrayMap<String, Job?>() }
    private val _jobs by _jobsDelegate

    fun launch(name: String, task: suspend () -> Unit) {
        // cancel previous job if exits
        _jobs[name]?.cancel()
        _jobs[name] = scope.launchWhenStarted {
            task()
        }
    }

    inline fun <T> collect(name: String, flow: Flow<T>, crossinline task: suspend (value: T) -> Unit) {
        launch(name) {
            flow.collect(task)
        }
    }

    fun cancel(name: String) {
        _jobs.remove(name)?.cancel()
    }

    @CallSuper
    open fun onRecycle() {
        if (_jobsDelegate.isInitialized()) {
            // Cancel all pending jobs, and clear
            _jobs.values.forEach { it?.cancel() }
            _jobs.clear()
        }
    }
}
