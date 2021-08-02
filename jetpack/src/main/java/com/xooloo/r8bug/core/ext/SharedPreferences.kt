package com.xooloo.r8bug.core.ext

import android.content.SharedPreferences

@JvmInline
@Suppress("unused")
value class PreferenceKey<T>(val key: String)

operator fun SharedPreferences.get(key: PreferenceKey<Boolean>, default: Boolean = false): Boolean =
    getBoolean(key.key, default)

operator fun SharedPreferences.Editor.set(
    key: PreferenceKey<Boolean>,
    value: Boolean
): SharedPreferences.Editor = putBoolean(key.key, value)

operator fun SharedPreferences.get(key: PreferenceKey<Int>, default: Int): Int =
    getInt(key.key, default)

operator fun SharedPreferences.Editor.set(
    key: PreferenceKey<Int>,
    value: Int
): SharedPreferences.Editor = putInt(key.key, value)

operator fun SharedPreferences.get(key: PreferenceKey<Long>, default: Long): Long =
    getLong(key.key, default)

operator fun SharedPreferences.Editor.set(
    key: PreferenceKey<Long>,
    value: Long
): SharedPreferences.Editor = putLong(key.key, value)

operator fun SharedPreferences.get(key: PreferenceKey<Float>, default: Float): Float =
    getFloat(key.key, default)

operator fun SharedPreferences.Editor.set(
    key: PreferenceKey<Float>,
    value: Float
): SharedPreferences.Editor = putFloat(key.key, value)

operator fun SharedPreferences.get(key: PreferenceKey<String>, default: String? = null): String? =
    getString(key.key, default)

operator fun SharedPreferences.Editor.set(
    key: PreferenceKey<String>,
    value: String?
): SharedPreferences.Editor = putString(key.key, value)


