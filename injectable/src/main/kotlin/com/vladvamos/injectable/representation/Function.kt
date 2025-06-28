package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.interfaces.Locatable
import com.vladvamos.injectable.representation.interfaces.Named

public class Function(
    override val fqName: String,
    public val annotations: List<Annotation>,
    override val coordinates: SourceCodeCoordinates? = null,
) : Named, Locatable {
    override fun toString(): String {
        return "Function(fqName=$fqName, annotations=$annotations, coordinates=$coordinates)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Function) {
            return false
        }
        return fqName == other.fqName &&
            annotations == other.annotations &&
            coordinates == other.coordinates
    }

    override fun hashCode(): Int {
        return fqName.hashCode() + 31 * (annotations.hashCode() + 31 * coordinates.hashCode())
    }
}
