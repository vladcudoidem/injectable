package com.vladvamos.injectable.representation.utils

data class DataModelExpectedValues(
    val classFqName: String,
    // One parameter list for each constructor.
    val constructorParameterLists: List<List<DataModelParameter>>,
)
