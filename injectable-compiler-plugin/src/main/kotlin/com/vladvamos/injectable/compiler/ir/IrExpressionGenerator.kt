package com.vladvamos.injectable.compiler.ir

import com.vladvamos.injectable.compiler.ir.data.ArgumentData
import com.vladvamos.injectable.compiler.ir.utils.reportError
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
interface IrExpressionGenerator {
    val expression: IrExpression
    val currentFile: IrFile
    val messageCollector: MessageCollector

    fun IrPluginContext.createFunctionCall(
        fqName: FqName,
        argumentsData: List<ArgumentData> = listOf(),
        functionSymbolFilter: (IrSimpleFunctionSymbol) -> Boolean = { true },
    ): IrExpression {
        val functionCallableId =
            CallableId(
                packageName = FqName(fqName.asString().substringBeforeLast(".")),
                callableName = Name.identifier(fqName.asString().substringAfterLast(".")),
            )
        val functionSymbols = referenceFunctions(functionCallableId).filter(functionSymbolFilter)

        if (functionSymbols.size > 1) {
            reportError { "Multiple declarations of '$fqName'." }
        } else if (functionSymbols.isEmpty()) {
            reportError { "'$fqName' was not found in the classpath." }
        }

        // At this point we know that there is only one match.
        val functionSymbol = functionSymbols.first()

        val functionCall =
            IrCallImpl.fromSymbolOwner(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                symbol = functionSymbol,
            )

        val parameters = functionSymbol.owner.parameters
        argumentsData.forEach {
            val index = it.locator.filter(parameters).indexInParameters
            functionCall.arguments[index] = it.expression
        }

        return functionCall
    }

    fun IrPluginContext.createConstructorCall(
        fqName: FqName,
        argumentsData: List<ArgumentData> = listOf(),
    ): IrExpression {
        val classSymbol = referenceClass(ClassId.topLevel(fqName))

        if (classSymbol == null) {
            reportError { "Cannot find class '$fqName'" }
        }
        classSymbol!!

        val constructorSymbol = classSymbol.constructors.first()

        val constructorCall =
            IrConstructorCallImpl.fromSymbolOwner(
                type = classSymbol.defaultType,
                constructorSymbol,
            )

        val parameters = constructorSymbol.owner.parameters
        argumentsData.forEach {
            val index = it.locator.filter(parameters).indexInParameters
            constructorCall.arguments[index] = it.expression
        }

        return constructorCall
    }

    private fun reportError(message: () -> String) {
        messageCollector.reportError(element = expression, currentFile, message)
    }
}
