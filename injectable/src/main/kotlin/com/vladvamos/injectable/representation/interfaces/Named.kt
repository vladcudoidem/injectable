package com.vladvamos.injectable.representation.interfaces

public interface Named {
    public val fqName: String?

    public val simpleName: String?
        get() = fqName?.split(".")?.last()
}
