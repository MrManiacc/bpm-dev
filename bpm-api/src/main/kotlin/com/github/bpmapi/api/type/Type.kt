package com.github.bpmapi.api.type

interface Type {
    val typeName: String
    val typeClass: Class<*>
}