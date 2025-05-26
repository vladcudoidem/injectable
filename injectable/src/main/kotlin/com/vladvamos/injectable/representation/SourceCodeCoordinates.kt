package com.vladvamos.injectable.representation

import java.nio.file.Path

public class SourceCodeCoordinates(
    public val file: Path?,
    public val position: Position?,
)
