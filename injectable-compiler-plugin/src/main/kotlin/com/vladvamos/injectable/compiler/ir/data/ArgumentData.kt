package com.vladvamos.injectable.compiler.ir.data

import org.jetbrains.kotlin.ir.expressions.IrExpression

data class ArgumentData(
    val locator: ParamLocator,
    val expression: IrExpression?,
)
