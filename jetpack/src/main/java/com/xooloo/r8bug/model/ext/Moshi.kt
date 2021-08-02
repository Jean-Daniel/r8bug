package com.xooloo.r8bug.model.ext

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


public inline fun <reified T> Moshi.adapter(): JsonAdapter<T> = adapter(T::class.java)
public inline fun <reified T> Moshi.toJson(value: T?): String = adapter(T::class.java).toJson(value)
public inline fun <reified T> Moshi.fromJson(value: String): T? =
    adapter(T::class.java).fromJson(value)

// For borken Json. Must not be used unless relly needed
public inline fun <reified T> Moshi.fromBrokenJson(value: String): T? =
    adapter(T::class.java).lenient().fromJson(value)


