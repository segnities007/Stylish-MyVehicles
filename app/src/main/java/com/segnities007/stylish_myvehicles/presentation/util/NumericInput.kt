package com.segnities007.stylish_myvehicles.presentation.util

fun String.normalizeIntegerInput(): String = buildString {
    this@normalizeIntegerInput.forEach { character ->
        val digit = Character.digit(character, 10)
        if (digit >= 0) append(digit)
    }
}

fun String.normalizeDecimalInput(): String = buildString {
    var hasDecimalPoint = false
    this@normalizeDecimalInput.forEach { character ->
        val digit = Character.digit(character, 10)
        when {
            digit >= 0 -> append(digit)
            (character == '.' || character == '．') && !hasDecimalPoint -> {
                append('.')
                hasDecimalPoint = true
            }
        }
    }
}
