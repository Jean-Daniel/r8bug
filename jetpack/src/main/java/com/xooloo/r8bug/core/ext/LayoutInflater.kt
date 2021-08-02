package com.xooloo.r8bug.core.ext

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method


private val methodMap = ArrayMap<Class<out ViewBinding>, Method>()

fun <VB : ViewBinding> LayoutInflater.inflate(cls: Class<VB>, parent: ViewGroup?, attachToRoot: Boolean = false): VB {
    val inflate = methodMap.getOrPut(cls, { cls.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java) })
    @Suppress("UNCHECKED_CAST")
    return inflate.invoke(null, this, parent, attachToRoot) as VB
}

inline fun <reified VB : ViewBinding> LayoutInflater.inflate(parent: ViewGroup?, attachToRoot: Boolean = false): VB {
    return inflate(VB::class.java, parent, attachToRoot)
}
