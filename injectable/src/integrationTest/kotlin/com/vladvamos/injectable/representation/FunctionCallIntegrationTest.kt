package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.utils.DataModelExpectedValues
import com.vladvamos.injectable.representation.utils.DataModelIntegrationTestBase
import com.vladvamos.injectable.representation.utils.DataModelParameter

class FunctionCallIntegrationTest :
    DataModelIntegrationTestBase(
        dataModelClass = FunctionCall::class,
        expectedValues =
            DataModelExpectedValues(
                classFqName = "com.vladvamos.injectable.representation.FunctionCall",
                constructorParameterLists =
                    listOf(
                        listOf(
                            DataModelParameter("coordinates", SourceCodeCoordinates::class),
                            DataModelParameter("function", Function::class),
                        )
                    )
            )
    )
