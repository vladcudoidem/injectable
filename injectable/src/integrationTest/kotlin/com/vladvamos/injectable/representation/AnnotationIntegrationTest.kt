package com.vladvamos.injectable.representation

import com.vladvamos.injectable.representation.utils.DataModelExpectedValues
import com.vladvamos.injectable.representation.utils.DataModelIntegrationTestBase
import com.vladvamos.injectable.representation.utils.DataModelParameter

class AnnotationIntegrationTest :
    DataModelIntegrationTestBase(
        dataModelClass = Annotation::class,
        expectedValues =
            DataModelExpectedValues(
                classFqName = "com.vladvamos.injectable.representation.Annotation",
                constructorParameterLists =
                    listOf(
                        listOf(
                            DataModelParameter("fqName", String::class),
                        )
                    )
            )
    )
