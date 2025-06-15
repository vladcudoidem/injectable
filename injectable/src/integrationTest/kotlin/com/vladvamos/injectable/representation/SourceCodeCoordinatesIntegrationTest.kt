package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.utils.DataModelExpectedValues
import com.vladvamos.injectable.representation.utils.DataModelIntegrationTestBase
import com.vladvamos.injectable.representation.utils.DataModelParameter
import java.nio.file.Path

class SourceCodeCoordinatesIntegrationTest :
    DataModelIntegrationTestBase(
        dataModelClass = SourceCodeCoordinates::class,
        expectedValues =
            DataModelExpectedValues(
                classFqName = "com.vladvamos.injectable.representation.SourceCodeCoordinates",
                constructorParameterLists =
                    listOf(
                        listOf(
                            DataModelParameter("file", Path::class),
                            DataModelParameter("position", Position::class),
                        )
                    )
            )
    )
