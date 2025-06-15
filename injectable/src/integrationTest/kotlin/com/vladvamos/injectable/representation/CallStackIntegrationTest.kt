package com.vladvamos.injectable.representation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CallStackIntegrationTest :
    FunSpec({
        // Todo: add tests for FqName and type parameter FunctionCall

        test("CallStack is a List") {
            val callStackClass = CallStack::class
            val callStackTypeFqName = callStackClass.qualifiedName

            callStackTypeFqName shouldBe "kotlin.collections.List"
        }
    })
