package com.xooloo.r8bug.core.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

// ==========================================
//              List Adapter
// ==========================================

val RecyclerView.ViewHolder.context: Context
    get() = itemView.context

// ==========================================
//      List Adapter With ViewBinding
// ==========================================
open class ViewBindingHolder<VB : ViewBinding>(val binding: VB) :
    RecyclerView.ViewHolder(binding.root) {
    val context: Context
        get() = itemView.context
}

