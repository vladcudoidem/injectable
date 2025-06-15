package com.vladvamos.injectable.compiler.ir.data

import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter

@JvmInline
value class ParamLocator(val filter: (List<IrValueParameter>) -> IrValueParameter) {
    companion object {
        val extensionReceiver = ParamLocator { parameterList ->
            parameterList.first { it.kind == IrParameterKind.ExtensionReceiver }
        }

        fun withName(name: String) = ParamLocator { parameterList ->
            parameterList.first { it.name.asString() == name }
        }
    }
}
