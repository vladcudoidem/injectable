package com.vladvamos.injectable.testing.representation

import com.vladvamos.injectable.representation.Annotation
import com.vladvamos.injectable.representation.FunctionCall
import com.vladvamos.injectable.representation.Position
import com.vladvamos.injectable.representation.SourceCodeCoordinates
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.io.path.Path

class InjectableExtensionsTest :
    DescribeSpec({
        describe("buildConsoleLink()") {
            it("correctly builds the link string") {
                val testCoordinates =
                    SourceCodeCoordinates(
                        file = Path("/path/to/File.kt"),
                        position = Position(row = 11, column = 22),
                    )
                val expectedCoordinates = "at /path/to/File.kt:11:22"

                testCoordinates.buildLink() shouldBe expectedCoordinates
            }
        }

        describe("CallStack.annotations") {
            it("includes all annotations of all functions") {
                fun buildAnnotationFqName(index: Int) = "my.example.Annotation$index"

                val callStack = buildList {
                    repeat(4) {
                        val functionCall = mockk<FunctionCall>()
                        val functionAnnotations =
                            List(it + 1) { listIndex ->
                                Annotation(fqName = buildAnnotationFqName(listIndex + 1))
                            }
                        every { functionCall.function.annotations } returns functionAnnotations

                        add(functionCall)
                    }
                }

                val expectedAnnotations =
                    (1..4).map { Annotation(fqName = buildAnnotationFqName(it)) }.toSet()

                callStack.annotations shouldBe expectedAnnotations
            }
        }
    })
