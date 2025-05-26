package com.vladvamos.injectable.modifier

import com.vladvamos.injectable.representation.CallStack

internal data class ComposableCallStackModifierState(
    val callStack: CallStack,
    val shouldAddToSemantics: Boolean,
)
