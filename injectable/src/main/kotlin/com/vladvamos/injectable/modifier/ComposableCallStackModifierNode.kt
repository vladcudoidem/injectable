package com.vladvamos.injectable.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import com.vladvamos.injectable.composableCallStack

internal class ComposableCallStackModifierNode(
    var modifierState: ComposableCallStackModifierState
) : Modifier.Node(), SemanticsModifierNode {

    override fun SemanticsPropertyReceiver.applySemantics() {
        with(modifierState) {
            if (shouldAddToSemantics) {
                // Todo: remove this.
                val callStackString = callStack.joinToString(", ") { it.function?.fqName ?: "null" }
                contentDescription = "callStack=[$callStackString]"

                composableCallStack = callStack
            }
        }
    }
}
