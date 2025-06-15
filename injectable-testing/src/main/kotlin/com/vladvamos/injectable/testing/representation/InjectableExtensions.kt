package com.vladvamos.injectable.testing.representation

import com.vladvamos.injectable.representation.Annotation
import com.vladvamos.injectable.representation.CallStack
import com.vladvamos.injectable.representation.FunctionCall
import com.vladvamos.injectable.representation.SourceCodeCoordinates
import kotlin.io.path.pathString

public fun SourceCodeCoordinates.buildLink(): String {
    val coordinates = "${file.pathString}:${position.row}:${position.column}"
    // The syntax 'at <...>' is required for link highlighting in the IDE.
    return "at $coordinates"
}

public fun FunctionCall.buildCallSiteLink(): String = coordinates.buildLink()

public fun FunctionCall.buildDeclarationSiteLink(): String? = function.coordinates?.buildLink()

public val CallStack.annotations: Set<Annotation>
    get() = flatMap { it.function.annotations }.toSet()
