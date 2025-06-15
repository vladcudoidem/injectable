package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.utils.DataModelExpectedValues
import com.vladvamos.injectable.representation.utils.DataModelIntegrationTestBase
import com.vladvamos.injectable.representation.utils.DataModelParameter

class FunctionIntegrationTest :
    DataModelIntegrationTestBase(
        dataModelClass = Function::class,
        expectedValues =
            DataModelExpectedValues(
                classFqName = "com.vladvamos.injectable.representation.Function",
                constructorParameterLists =
                    listOf(
                        listOf(
                            DataModelParameter("fqName", String::class),
                            DataModelParameter("annotations", List::class),
                            DataModelParameter("coordinates", SourceCodeCoordinates::class),
                        )
                    )
            )
    )
