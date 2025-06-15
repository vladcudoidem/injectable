package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.utils.DataModelExpectedValues
import com.vladvamos.injectable.representation.utils.DataModelIntegrationTestBase
import com.vladvamos.injectable.representation.utils.DataModelParameter

class PositionIntegrationTest :
    DataModelIntegrationTestBase(
        dataModelClass = Position::class,
        expectedValues =
            DataModelExpectedValues(
                classFqName = "com.vladvamos.injectable.representation.Position",
                constructorParameterLists =
                    listOf(
                        listOf(
                            DataModelParameter("row", Int::class),
                            DataModelParameter("column", Int::class),
                        )
                    )
            )
    )
