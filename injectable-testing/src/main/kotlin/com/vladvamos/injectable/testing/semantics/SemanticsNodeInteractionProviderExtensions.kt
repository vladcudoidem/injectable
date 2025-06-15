package com.vladvamos.injectable.testing.semantics

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onRoot
import com.vladvamos.injectable.representation.CallStack

public fun SemanticsNodeInteractionsProvider.onAllNodes(
    action: (SemanticsNode, CallStack?) -> Unit,
) {
    val rootNode = onRoot(useUnmergedTree = true).fetchSemanticsNode()
    rootNode.onThisAndDescendants { action(it, it.callStack) }
}

public fun SemanticsNodeInteractionsProvider.onAllNodesWithCallStack(
    action: (SemanticsNode, CallStack) -> Unit,
) = onAllNodes { semanticsNode, callStack ->
    if (callStack != null) {
        action(semanticsNode, callStack)
    }
}
