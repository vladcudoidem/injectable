package com.vladvamos.injectable.modifier

import androidx.compose.ui.Modifier
import com.vladvamos.injectable.representation.FunctionCall
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter

class ModifierExtensionsIntegrationTest :
    DescribeSpec({
        describe("registerCall()") {
            // Getting the qualified name of a global function is not possible.
            it("has the right simple name") {
                val registerCallFunction = Modifier::registerCall
                val registerCallSimpleName = registerCallFunction.name

                registerCallSimpleName shouldBe "registerCall"
            }

            val modifierClassFqName = "androidx.compose.ui.Modifier"

            it("has Modifier as receiver type") {
                val registerCallFunction = Modifier::registerCall
                val receiverTypeClassifier =
                    registerCallFunction.extensionReceiverParameter!!.type.classifier!!

                receiverTypeClassifier as KClass<*>

                val receiverTypeFqName = receiverTypeClassifier.qualifiedName!!

                receiverTypeFqName shouldBe modifierClassFqName
            }

            it("has Modifier as return type") {
                val registerCallFunction = Modifier::registerCall
                val returnTypeClassifier = registerCallFunction.returnType.classifier!!

                returnTypeClassifier as KClass<*>

                val returnTypeFqName = returnTypeClassifier.qualifiedName!!

                returnTypeFqName shouldBe modifierClassFqName
            }

            it("has one parameter: 'call' of type FunctionCall") {
                val registerCallFunction = Modifier::registerCall
                val regularParameters =
                    registerCallFunction.parameters.filter { it.kind == KParameter.Kind.VALUE }

                regularParameters shouldHaveSize 1

                val regularParameter = regularParameters.first()
                val parameterName = regularParameter.name
                val parameterTypeClass = regularParameter.type.classifier!! as KClass<*>

                parameterName shouldBe "call"
                parameterTypeClass shouldBe FunctionCall::class
            }
        }
    })
