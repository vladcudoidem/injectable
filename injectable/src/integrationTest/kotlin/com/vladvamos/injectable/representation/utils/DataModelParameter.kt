package com.vladvamos.injectable.representation.utils

import kotlin.reflect.KClass

data class DataModelParameter(
    val name: String,
    val typeClass: KClass<*>,
)
