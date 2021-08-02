package com.xooloo.r8bug.model.ext

import java.nio.ByteBuffer
import java.util.*


// ByteBuffer byte order is always big endian (unless explicitly changed)
public fun UUID.toBytes(): ByteArray = ByteBuffer.wrap(ByteArray(16)).apply {
    putLong(mostSignificantBits)
    putLong(leastSignificantBits)
}.array()

@Suppress("FunctionName", "UsePropertyAccessSyntax")
public fun UUID(bytes: ByteArray): UUID {
    check(bytes.size == 16)
    val bb: ByteBuffer = ByteBuffer.wrap(bytes)
    val high: Long = bb.getLong()
    val low: Long = bb.getLong()
    return UUID(high, low)
}

@Suppress("FunctionName", "UsePropertyAccessSyntax")
public fun UUID(string: String): UUID? {
    return runCatching { UUID.fromString(string) }.getOrNull()
}

