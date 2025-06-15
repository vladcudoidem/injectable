package com.vladvamos.injectable.compiler.ir

import com.vladvamos.injectable.compiler.ir.data.ArgumentData
import com.vladvamos.injectable.compiler.ir.data.ParamLocator
import com.vladvamos.injectable.compiler.ir.utils.reportError
import com.vladvamos.injectable.compiler.utils.clearBit
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCompositeImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

// Todo: make reporting better
// Todo: replace '*.!!' with direct returns (and wrap results)

class ModifierExpressionTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector,
) : IrElementTransformerVoid() {
    private val modifierClassFqName = FqName("androidx.compose.ui.Modifier")

    // Used for reporting.
    lateinit var currentFile: IrFile

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFile(declaration: IrFile): IrFile {
        currentFile = declaration
        return super.visitFile(declaration)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall): IrExpression {
        fun propagate() = super.visitCall(expression)

        val owner = expression.symbol.owner

        val composableAnnotationFqName = FqName("androidx.compose.runtime.Composable")
        val isComposable = owner.hasAnnotation(composableAnnotationFqName)
        if (!isComposable) {
            return propagate()
        }

        val indexesOfModifierParameters =
            owner.parameters
                .filter { it.type.classFqName == modifierClassFqName }
                .map { it.indexInParameters }
        if (indexesOfModifierParameters.isEmpty()) {
            return propagate()
        }

        val indexToModifierArgumentMap =
            indexesOfModifierParameters.associateWith { expression.arguments[it] }

        val indexToUpdatedModifierArgumentMap =
            indexToModifierArgumentMap.mapValues { (index, modifierArgument) ->
                if (modifierArgument is IrCompositeImpl) {
                    expression.enableArgumentInDefaultParameter(index)
                }
                modifierArgument.addSemanticInformation(call = expression)
            }

        indexToUpdatedModifierArgumentMap.forEach { (index, argument) ->
            expression.arguments[index] = argument
        }

        return propagate()
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrCall.enableArgumentInDefaultParameter(index: Int) {
        val owner = symbol.owner

        val indexOfDefault =
            owner.parameters.first { it.name.asString() == "\$default" }.indexInParameters
        val expressionOfDefault = arguments[indexOfDefault] as? IrConstImpl
        val valueOfDefault = expressionOfDefault?.value as? Int

        if (valueOfDefault == null) {
            messageCollector.reportError(this, currentFile) { "Cannot read '\$default' parameter" }
        }
        valueOfDefault!!

        val updatedValueOfDefault = valueOfDefault clearBit index

        arguments[indexOfDefault] =
            IrConstImpl.int(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = pluginContext.irBuiltIns.intType,
                value = updatedValueOfDefault,
            )
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrExpression?.addSemanticInformation(call: IrCall): IrExpression? {
        val baseArgumentExpression = this

        val registerCallGenerator =
            object : IrExpressionGenerator {
                override val expression = call
                override val currentFile = this@ModifierExpressionTransformer.currentFile
                override val messageCollector = this@ModifierExpressionTransformer.messageCollector
            }
        val argumentsData =
            listOf(
                ArgumentData(
                    locator = ParamLocator.extensionReceiver,
                    expression =
                        if (baseArgumentExpression !is IrCompositeImpl) {
                            baseArgumentExpression
                        } else {
                            createModifierObjectReference()
                        }
                ),
                ArgumentData(
                    locator = ParamLocator.withName(name = "call"),
                    expression =
                        SemanticInformationGenerator.createExpression(
                            pluginContext,
                            expression = call,
                            currentFile,
                            messageCollector
                        )
                )
            )

        val registerCall =
            with(registerCallGenerator) {
                pluginContext.createFunctionCall(
                    FqName("com.vladvamos.injectable.modifier.registerCall"),
                    argumentsData = argumentsData,
                )
            }

        return registerCall
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun createModifierObjectReference(): IrGetObjectValueImpl {
        val modifierClass = pluginContext.referenceClass(ClassId.topLevel(modifierClassFqName))
        if (modifierClass == null) {
            messageCollector.reportError { "Definition of '$modifierClassFqName' was not found." }
        }
        modifierClass!!

        val modifierCompanionObject = modifierClass.owner.companionObject()
        if (modifierCompanionObject == null) {
            messageCollector.reportError {
                "Cannot find companion object of '$modifierClassFqName'."
            }
        }
        modifierCompanionObject!!

        val companionObjectReference =
            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = modifierCompanionObject.defaultType,
                symbol = modifierCompanionObject.symbol,
            )

        return companionObjectReference
    }
}
