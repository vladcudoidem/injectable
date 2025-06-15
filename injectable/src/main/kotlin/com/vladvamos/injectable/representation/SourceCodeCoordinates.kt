package com.vladvamos.injectable.representation

import java.nio.file.Path

public class SourceCodeCoordinates(
    public val file: Path,
    public val position: Position,
) {
    override fun toString(): String {
        return "SourceCodeCoordinates(file=$file, position=$position)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SourceCodeCoordinates) {
            return false
        }
        return file == other.file && position == other.position
    }

    override fun hashCode(): Int {
        return 31 * file.hashCode() + position.hashCode()
    }
}
