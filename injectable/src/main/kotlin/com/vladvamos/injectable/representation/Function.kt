package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.interfaces.Locatable
import com.vladvamos.injectable.representation.interfaces.Named

public class Function(
    override val fqName: String?,
    public val annotations: List<Annotation>?,
    override val coordinates: SourceCodeCoordinates?,
) : Named, Locatable
