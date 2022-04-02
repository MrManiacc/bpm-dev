package com.github.bpmapi.api.type

enum class SystemType(override val typeName: String, override val typeClass: Class<*>) : Type {
    BOOLEAN("Boolean", Boolean::class.javaObjectType),
    INT("Int", Int::class.javaObjectType),
    FLOAT("Float", Float::class.javaObjectType),
    STRING("String", Double::class.javaObjectType),
}