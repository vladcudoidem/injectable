package com.vladvamos.injectable.testing.semantics

import androidx.compose.ui.test.SemanticsNodeInteraction
import com.vladvamos.injectable.representation.CallStack

public val SemanticsNodeInteraction.callStack: CallStack?
    get() = fetchSemanticsNode().callStack
