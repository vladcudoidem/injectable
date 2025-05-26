package com.vladvamos.injectable

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import com.vladvamos.injectable.representation.CallStack

public val ComposableCallStackKey: SemanticsPropertyKey<CallStack> =
    SemanticsPropertyKey(
        name = "ComposableCallStack",
        // Todo: fitting merge policy?
        mergePolicy = { parentValue, childValue -> parentValue },
    )

public var SemanticsPropertyReceiver.composableCallStack by ComposableCallStackKey
