package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.render.Renderer

class NodeWrapper<T : Node>(
    val name: String,
    val group: String = "base",
    private val nodeSupplier: () -> T,
    private val renderSupplier: Renderer<T>
) {
    fun new(): T = nodeSupplier()
    operator fun invoke(): T = new()
    fun render(node: T) = renderSupplier.render(node)
}