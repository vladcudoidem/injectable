package com.vladvamos.injectable.representation

public class Position(public val row: Int, public val column: Int) {
    override fun toString(): String {
        return "Position(row=$row, column=$column)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Position) {
            return false
        }
        return row == other.row && column == other.column
    }

    override fun hashCode(): Int {
        return 31 * row.hashCode() + column.hashCode()
    }
}
