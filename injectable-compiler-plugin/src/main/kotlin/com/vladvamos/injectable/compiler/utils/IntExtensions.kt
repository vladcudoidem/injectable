package com.vladvamos.injectable.compiler.utils

internal fun Int.changeBit(index: Int, value: Int): Int =
    when (value) {
        0 -> this and (1 shl index).inv()
        1 -> this or (1 shl index)
        else -> {
            throw IllegalArgumentException("'value' is required to be 0 or 1")
        }
    }

internal infix fun Int.clearBit(index: Int): Int = changeBit(index, value = 0)
