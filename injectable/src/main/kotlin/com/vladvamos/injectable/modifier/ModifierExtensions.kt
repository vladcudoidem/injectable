package com.vladvamos.injectable.modifier

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import com.vladvamos.injectable.representation.FunctionCall

// Todo: require opt-in for usage

/**
 * Adds the [call] to the [com.vladvamos.injectable.representation.CallStack] of this Modifier
 * chain.
 *
 * The *injectable* compiler plugin internally uses it to inject semantic information into
 * composables. It is **not** advised to call this extension directly.
 */
public fun Modifier.registerCall(call: FunctionCall): Modifier {
    val lastCallStackModifier = getLastCallStackModifier()
    val existsCallStackModifier = lastCallStackModifier != null

    if (existsCallStackModifier) {
        lastCallStackModifier.addToSemantics = false
    }
    val currentCallStack =
        if (existsCallStackModifier) {
            lastCallStackModifier.callStack + call
        } else {
            listOf(call)
        }

    return this then ComposableCallStackModifier(currentCallStack)
}

@SuppressLint("ModifierFactoryReturnType")
internal fun Modifier.getLastCallStackModifier(): ComposableCallStackModifier? {
    var lastModifier: ComposableCallStackModifier? = null
    var lastModifierFound = false

    // Abuse the folding operation to traverse the Modifier structure. Use `foldOut` to start at the
    // last Modifier in the Modifier chain and get the first match.
    foldOut(Unit) { modifier, _ ->
        if (!lastModifierFound && modifier is ComposableCallStackModifier) {
            lastModifier = modifier
            lastModifierFound = true
        }
    }

    return lastModifier
}
