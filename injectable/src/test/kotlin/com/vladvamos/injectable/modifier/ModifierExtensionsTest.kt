package com.vladvamos.injectable.modifier

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.vladvamos.injectable.representation.Annotation
import com.vladvamos.injectable.representation.Function
import com.vladvamos.injectable.representation.FunctionCall
import com.vladvamos.injectable.representation.Position
import com.vladvamos.injectable.representation.SourceCodeCoordinates
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path

class ModifierExtensionsTest :
    DescribeSpec({
        describe("registerCall()") {
            it("correctly creates callStack") {
                val modifier = createComplexTestModifier()
                val lastCallStack = modifier.getLastCallStackModifier()!!.callStack

                // A CallStack is just a List of FunctionCalls.
                lastCallStack shouldBe testFunctionCalls
            }

            it("keeps only last ComposableCallStackModifier active") {
                val modifier = createComplexTestModifier()

                val activeFlags = mutableListOf<Boolean>()
                modifier.foldIn(Unit) { _, modifier ->
                    if (modifier is ComposableCallStackModifier) {
                        val isActive = modifier.addToSemantics
                        activeFlags.add(isActive)
                    }
                }

                activeFlags.last() shouldBe true
                activeFlags.dropLast(1).forEach { it shouldBe false }
            }
        }
    })

private val testFunctionCalls =
    listOf(
        FunctionCall(
            SourceCodeCoordinates(Path("A.kt"), Position(1, 1)),
            Function(
                "AComposable",
                listOf(Annotation("ComposableA")),
                SourceCodeCoordinates(Path("AS.kt"), Position(11, 11))
            )
        ),
        FunctionCall(
            SourceCodeCoordinates(Path("B.kt"), Position(2, 2)),
            Function(
                "BComposable",
                listOf(Annotation("ComposableB")),
                SourceCodeCoordinates(Path("BS.kt"), Position(22, 22))
            )
        ),
        FunctionCall(
            SourceCodeCoordinates(Path("C.kt"), Position(3, 3)),
            Function(
                "CComposable",
                listOf(Annotation("ComposableC")),
                SourceCodeCoordinates(Path("CS.kt"), Position(33, 33))
            )
        ),
    )

// Creates a Modifier for testing by calling Modifier.registerCall() with each of the FunctionCalls
// in order. Also adds some other Modifiers in between.
@SuppressLint("ModifierFactoryExtensionFunction")
private fun createComplexTestModifier(): Modifier {
    var modifier: Modifier = Modifier
    testFunctionCalls.forEach { modifier = modifier.registerCall(it).testTag("myTestTag") }

    return modifier
}
