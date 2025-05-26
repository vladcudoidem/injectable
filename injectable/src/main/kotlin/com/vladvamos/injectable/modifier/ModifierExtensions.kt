package com.vladvamos.injectable.modifier

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import com.vladvamos.injectable.representation.Function
import com.vladvamos.injectable.representation.FunctionCall

// Todo: remove overload
public fun Modifier.registerCall(fqName: String): Modifier {
    val function =
        Function(
            fqName = fqName,
            annotations = null,
            coordinates = null,
        )

    return registerCall(
        FunctionCall(
            coordinates = null,
            function = function,
        )
    )
}

public fun Modifier.registerCall(call: FunctionCall): Modifier {
    val lastCallStackModifier = getLastCallStackModifier()
    return if (lastCallStackModifier != null) {
        lastCallStackModifier.addToSemantics = false
        this then ComposableCallStackModifier(callStack = lastCallStackModifier.callStack + call)
    } else {
        this then ComposableCallStackModifier(listOf(call))
    }
}

@SuppressLint("ModifierFactoryReturnType")
private fun Modifier.getLastCallStackModifier(): ComposableCallStackModifier? {
    var lastModifier: ComposableCallStackModifier? = null
    foldIn(Unit) { _, modifier ->
        if (modifier is ComposableCallStackModifier) {
            lastModifier = modifier
            return@foldIn
        }
    }

    return lastModifier
}
