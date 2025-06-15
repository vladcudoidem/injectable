package com.vladvamos.injectable.representation.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

abstract class DataModelIntegrationTestBase(
    dataModelClass: KClass<*>,
    expectedValues: DataModelExpectedValues,
    body: FunSpec.() -> Unit = {},
) :
    FunSpec({
        val dataModelClassSimpleName = dataModelClass.simpleName

        test("$dataModelClassSimpleName has the right fq name") {
            val actualClassFqName = dataModelClass.qualifiedName

            actualClassFqName shouldBe expectedValues.classFqName
        }

        val expectedConstructorCount = expectedValues.constructorParameterLists.size
        test("$dataModelClassSimpleName has $expectedConstructorCount constructors") {
            val actualClassConstructors = dataModelClass.constructors

            actualClassConstructors shouldHaveSize expectedConstructorCount
        }

        val constructors = dataModelClass.constructors.toList()
        expectedValues.constructorParameterLists.forEachIndexed { index, parameters ->
            val parameterCount = parameters.size

            val testNamePrefix =
                "$dataModelClassSimpleName's nr. ${index + 1} constructor has $parameterCount parameters"
            val testNameSuffix =
                parameters.joinToString(", ") { "'${it.name}' of type ${it.typeClass.simpleName}" }
            val testName = "$testNamePrefix: $testNameSuffix"

            test(testName) {
                val constructor = constructors[index]
                val regularConstructorParameters =
                    constructor.parameters.filter { it.kind == KParameter.Kind.VALUE }

                regularConstructorParameters shouldHaveSize parameterCount

                val parameterNameToTypeClass =
                    regularConstructorParameters.map {
                        it.name!! to it.type.classifier!! as KClass<*>
                    }
                val expectedParameterNameToTypeClass = parameters.map { it.name to it.typeClass }

                parameterNameToTypeClass shouldBe expectedParameterNameToTypeClass
            }
        }

        body()
    })
