package com.vladvamos.injectable.compiler.ir

import com.vladvamos.injectable.compiler.ir.data.ArgumentData
import com.vladvamos.injectable.compiler.ir.data.ParamLocator
import com.vladvamos.injectable.compiler.ir.utils.reportError
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

// Todo: make reporting consistent

@OptIn(UnsafeDuringIrConstructionAPI::class)
class SemanticInformationGenerator
private constructor(
    private val pluginContext: IrPluginContext,
    override val expression: IrCall,
    override val currentFile: IrFile,
    override val messageCollector: MessageCollector,
) : IrExpressionGenerator {
    private val annotationClassFqName = FqName("com.vladvamos.injectable.representation.Annotation")

    private fun createFunctionCallConstructorCall(): IrExpression =
        pluginContext.createConstructorCall(
            FqName("com.vladvamos.injectable.representation.FunctionCall"),
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("coordinates"),
                        expression =
                            createSourceCodeCoordinatesConstructorCall(
                                startOffset = expression.startOffset,
                                file = currentFile
                            )
                    ),
                    ArgumentData(
                        locator = ParamLocator.withName("function"),
                        expression = createFunctionConstructorCall()
                    ),
                )
        )

    private fun createSourceCodeCoordinatesConstructorCall(
        startOffset: Int,
        file: IrFile,
    ): IrExpression {
        val row = file.fileEntry.getLineNumber(startOffset) + 1
        val column = file.fileEntry.getColumnNumber(startOffset) + 1

        return pluginContext.createConstructorCall(
            FqName("com.vladvamos.injectable.representation.SourceCodeCoordinates"),
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("file"),
                        expression = createPathFunctionCall(file)
                    ),
                    ArgumentData(
                        locator = ParamLocator.withName("position"),
                        expression = createPositionConstructorCall(row, column)
                    ),
                )
        )
    }

    private fun createPathFunctionCall(file: IrFile): IrExpression =
        pluginContext.createFunctionCall(
            FqName("kotlin.io.path.Path"),
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("path"),
                        expression = buildStringExpression(file.path)
                    )
                ),
            functionSymbolFilter = {
                val parameterNames =
                    it.owner.parameters.map { parameter -> parameter.name.asString() }

                "path" in parameterNames
            }
        )

    private fun createPositionConstructorCall(row: Int, column: Int): IrExpression =
        pluginContext.createConstructorCall(
            FqName("com.vladvamos.injectable.representation.Position"),
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("row"),
                        expression = buildIntExpression(row)
                    ),
                    ArgumentData(
                        locator = ParamLocator.withName("column"),
                        expression = buildIntExpression(column)
                    )
                )
        )

    private fun createFunctionConstructorCall(): IrExpression {
        val argumentsData = buildList {
            addAll(
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("fqName"),
                        expression =
                            buildStringExpression(expression.symbol.owner.kotlinFqName.asString())
                    ),
                    ArgumentData(
                        locator = ParamLocator.withName("annotations"),
                        expression = createListOfAnnotationsFunctionCall()
                    )
                )
            )

            val symbolOwner = expression.symbol.owner
            val isOwnerSourceResolvable =
                try {
                    // Todo: is there another way to do this?
                    symbolOwner.fileEntry
                    symbolOwner.file
                    true
                } catch (_: Throwable) {
                    false
                }

            if (isOwnerSourceResolvable) {
                add(
                    ArgumentData(
                        locator = ParamLocator.withName("coordinates"),
                        expression =
                            createSourceCodeCoordinatesConstructorCall(
                                startOffset = symbolOwner.startOffset,
                                file = symbolOwner.file
                            )
                    )
                )
            }
        }

        return pluginContext.createConstructorCall(
            FqName("com.vladvamos.injectable.representation.Function"),
            argumentsData
        )
    }

    private fun createListOfAnnotationsFunctionCall(): IrExpression =
        pluginContext.createFunctionCall(
            FqName("kotlin.collections.listOf"),
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("elements"),
                        expression = createAnnotationsVarargExpression()
                    )
                ),
            functionSymbolFilter = {
                val regularParameters =
                    it.owner.parameters.filter { it.kind == IrParameterKind.Regular }

                regularParameters.size == 1 && regularParameters.first().isVararg
            }
        )

    private fun createAnnotationsVarargExpression(): IrExpression {
        val annotationClass = pluginContext.referenceClass(ClassId.topLevel(annotationClassFqName))
        if (annotationClass == null) {
            messageCollector.reportError {
                "Cannot find class '${annotationClassFqName.asString()}'"
            }
        }
        annotationClass!!
        val annotationType = annotationClass.defaultType

        val annotationObjects =
            expression.symbol.owner.annotations.mapNotNull { annotationConstructorCall ->
                val annotationFqName = annotationConstructorCall.type.classFqName?.asString()
                annotationFqName?.let { createAnnotationConstructorCall(it) }
            }

        return IrVarargImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = pluginContext.irBuiltIns.arrayClass.typeWith(annotationType),
            varargElementType = annotationType,
            elements = annotationObjects,
        )
    }

    private fun createAnnotationConstructorCall(annotationFqName: String): IrExpression =
        pluginContext.createConstructorCall(
            fqName = annotationClassFqName,
            argumentsData =
                listOf(
                    ArgumentData(
                        locator = ParamLocator.withName("fqName"),
                        expression = buildStringExpression(annotationFqName)
                    )
                )
        )

    private fun buildIntExpression(value: Int) =
        IrConstImpl.int(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = pluginContext.irBuiltIns.intType,
            value,
        )

    private fun buildStringExpression(value: String) =
        IrConstImpl.string(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = pluginContext.irBuiltIns.stringType,
            value,
        )

    companion object {
        fun createExpression(
            pluginContext: IrPluginContext,
            expression: IrCall,
            currentFile: IrFile,
            messageCollector: MessageCollector,
        ): IrExpression {
            val dataGenerator =
                SemanticInformationGenerator(
                    pluginContext,
                    expression,
                    currentFile,
                    messageCollector
                )

            return dataGenerator.createFunctionCallConstructorCall()
        }
    }
}
