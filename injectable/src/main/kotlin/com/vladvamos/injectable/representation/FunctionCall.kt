package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.interfaces.Locatable

public class FunctionCall(
    override val coordinates: SourceCodeCoordinates?,
    public val function: Function?,
) : Locatable
