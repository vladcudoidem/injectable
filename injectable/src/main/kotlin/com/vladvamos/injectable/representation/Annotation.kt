package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.interfaces.Named

public class Annotation(override val fqName: String) : Named {
    override fun toString(): String {
        return "Annotation(fqName=$fqName)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Annotation) {
            return false
        }
        return fqName == other.fqName
    }

    override fun hashCode(): Int {
        return fqName.hashCode()
    }
}
