package com.vladvamos.injectable.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCompositeImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ModifierExpressionTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector,
) : IrElementTransformerVoid() {

    private val composableAnnotationFqName = FqName("androidx.compose.runtime.Composable")
    private val modifierClassFqName = FqName("androidx.compose.ui.Modifier")
    private val modifierExtensionFqName = FqName("com.vladvamos.injectable.modifier.registerCall")
    private val stringClassFqName = FqName("kotlin.String")

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
        val isComposable = owner.hasAnnotation(composableAnnotationFqName)
        if (!isComposable) {
            return propagate()
        }

        val indexesOfModifierValueParameters =
            owner.parameters.mapNotNull {
                if (it.type.classFqName == modifierClassFqName) {
                    it.indexInParameters
                } else {
                    null
                }
            }

        if (indexesOfModifierValueParameters.isEmpty()) {
            return propagate()
        }

        val indexToModifierArgumentMap =
            indexesOfModifierValueParameters.associateWith { expression.arguments[it] }

        val indexToUpdatedModifierArgumentMap =
            indexToModifierArgumentMap.mapValues { (_, argument) ->
                reportWarning(expression.startOffset) { "Extending Modifier expression." }

                extendModifierArgument(
                    parent = expression,
                    baseExpression = argument,
                )
            }

        indexToUpdatedModifierArgumentMap.forEach { (index, argument) ->
            expression.arguments[index] = argument
        }

        return propagate()
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun extendModifierArgument(
        parent: IrCall,
        baseExpression: IrExpression?,
    ): IrExpression? {
        val extensionFunctionSymbol =
            pluginContext
                .referenceFunctions(
                    with(modifierExtensionFqName.toString()) {
                        CallableId(
                            packageName = FqName(substringBeforeLast(".")),
                            callableName = Name.identifier(substringAfterLast(".")),
                        )
                    }
                )
                .filter { functionSymbol ->
                    val regularParameters =
                        functionSymbol.owner.parameters.filter {
                            it.kind == IrParameterKind.Regular
                        }

                    // Todo: change this later
                    regularParameters.any {
                        it.name.asString() == "fqName" && it.type.classFqName == stringClassFqName
                    }
                }
                .let {
                    if (it.size > 1) {
                        messageCollector.report(
                            severity = CompilerMessageSeverity.ERROR,
                            message =
                                "Multiple declarations of '$modifierExtensionFqName'. " +
                                    "Correct behaviour of the injectable compiler plugin is not " +
                                    "guaranteed.",
                        )
                        return baseExpression
                    } else if (it.isEmpty()) {
                        messageCollector.report(
                            severity = CompilerMessageSeverity.ERROR,
                            message = "'$modifierExtensionFqName' was not found in the classpath.",
                        )
                        return baseExpression
                    }

                    // if there is exactly one definition found, return that
                    it.first()
                }

        val extendedCall =
            IrCallImpl.fromSymbolOwner(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    symbol = extensionFunctionSymbol,
                )
                .apply {
                    extensionReceiver =
                        if (baseExpression !is IrCompositeImpl) {
                            baseExpression
                        } else {
                            createModifierObjectReference()
                        }

                    arguments[1] =
                        IrConstImpl.string(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = pluginContext.irBuiltIns.stringType,
                            // This represents the injected information.
                            value = parent.symbol.owner.name.asString(),
                        )
                }

        return extendedCall
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun createModifierObjectReference(): IrGetObjectValueImpl {
        val modifierClass =
            pluginContext.referenceClass(ClassId.topLevel(modifierClassFqName)).let {
                if (it == null) {
                    messageCollector.report(
                        severity = CompilerMessageSeverity.ERROR,
                        message = "Definition of '$modifierClassFqName' was not found.",
                    )
                }
                it!!
            }

        val companionObject =
            modifierClass.owner.companionObject().let {
                if (it == null) {
                    messageCollector.report(
                        severity = CompilerMessageSeverity.ERROR,
                        message = "'$modifierClassFqName' does not have a companion object.",
                    )
                }
                it!!
            }

        val companionObjectReference =
            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = companionObject.defaultType,
                symbol = companionObject.symbol,
            )

        return companionObjectReference
    }

    // Used for debugging.
    private fun reportWarning(startOffset: Int, message: () -> String) {
        messageCollector.report(
            severity = CompilerMessageSeverity.WARNING,
            message = message(),
            location =
                CompilerMessageLocation.create(
                    path = currentFile.path,
                    line = currentFile.fileEntry.getLineNumber(startOffset) + 1,
                    column = currentFile.fileEntry.getColumnNumber(startOffset) + 1,
                    lineContent = null,
                )
        )
    }
}
