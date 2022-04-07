package com.github.bpmapi.api.graph.render

fun interface Renderer<T : Any> {
    fun render(value: T)
}