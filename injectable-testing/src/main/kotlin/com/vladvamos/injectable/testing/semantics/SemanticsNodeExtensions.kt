package com.vladvamos.injectable.testing.semantics

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.getOrNull
import com.vladvamos.injectable.representation.CallStack
import com.vladvamos.injectable.semantics.ComposableCallStackKey

public val SemanticsNode.callStack: CallStack?
    get() = config.getOrNull(ComposableCallStackKey)

public fun SemanticsNode.onThisAndDescendants(action: (SemanticsNode) -> Unit) {
    action(this)
    children.forEach { it.onThisAndDescendants(action) }
}
