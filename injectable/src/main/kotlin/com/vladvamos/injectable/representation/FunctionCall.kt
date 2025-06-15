package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.interfaces.Locatable

public class FunctionCall(
    override val coordinates: SourceCodeCoordinates,
    public val function: Function,
) : Locatable {
    override fun toString(): String {
        return "FunctionCall(coordinates=$coordinates, function=$function)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is FunctionCall) {
            return false
        }
        return coordinates == other.coordinates && function == other.function
    }

    override fun hashCode(): Int {
        return coordinates.hashCode() + 31 * function.hashCode()
    }
}
