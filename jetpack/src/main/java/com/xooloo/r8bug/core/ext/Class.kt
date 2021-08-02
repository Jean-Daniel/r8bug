package com.xooloo.r8bug.core.ext

import androidx.collection.ArrayMap
import java.lang.reflect.ParameterizedType

private fun Class<*>.parametrizedParent(): ParameterizedType? {
    // Recurse
    var cls: Class<*>? = this
    while (cls != null) {
        val type = cls.genericSuperclass
        if (type is ParameterizedType)
            return type
        cls = cls.superclass
    }
    return null
}

private val parameterMap = ArrayMap<Class<*>, ParameterizedType>()

fun <T> Class<*>.parameterType(): Class<T> {
    val type = parameterMap.getOrPut(this, {
        parametrizedParent() ?: error("must be a parametrized subclass")
    })
    @Suppress("UNCHECKED_CAST")
    return type.actualTypeArguments[0] as Class<T>
}
