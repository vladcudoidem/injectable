package com.vladvamos.injectable.compiler.ir.utils

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocationWithRange
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.path

internal fun MessageCollector.reportError(
    element: IrElement? = null,
    currentFile: IrFile? = null,
    message: () -> String,
) {
    val messageLocation =
        if (element != null && currentFile != null) {
            val startOffset = element.startOffset
            val endOffset = element.endOffset

            with(currentFile) {
                CompilerMessageLocationWithRange.create(
                    path = path,
                    lineStart = adjustedLineNumber(startOffset),
                    columnStart = adjustedColumnNumber(startOffset),
                    lineEnd = adjustedLineNumber(endOffset),
                    columnEnd = adjustedColumnNumber(endOffset),
                    lineContent = null,
                )
            }
        } else {
            null
        }

    report(
        severity = CompilerMessageSeverity.ERROR,
        message = message(),
        location = messageLocation,
    )
}

private fun IrFile.adjustedLineNumber(offset: Int) = fileEntry.getLineNumber(offset) + 1

private fun IrFile.adjustedColumnNumber(offset: Int) = fileEntry.getColumnNumber(offset) + 1
