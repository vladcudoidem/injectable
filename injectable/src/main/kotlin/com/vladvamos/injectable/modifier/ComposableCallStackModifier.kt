package com.vladvamos.injectable.modifier

import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import com.vladvamos.injectable.representation.CallStack

internal class ComposableCallStackModifier(val callStack: CallStack) :
    ModifierNodeElement<ComposableCallStackModifierNode>() {

    var addToSemantics = true

    val currentModifierState: ComposableCallStackModifierState
        get() = ComposableCallStackModifierState(callStack, addToSemantics)

    override fun create(): ComposableCallStackModifierNode {
        return ComposableCallStackModifierNode(currentModifierState)
    }

    override fun update(node: ComposableCallStackModifierNode) {
        node.modifierState = currentModifierState
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "registerCall"
        properties["composableCallStackModifierState"] = currentModifierState
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ComposableCallStackModifier) return false
        // Todo: implement equals() for classes.
        return currentModifierState == other.currentModifierState
    }

    override fun hashCode(): Int {
        return currentModifierState.hashCode()
    }
}
