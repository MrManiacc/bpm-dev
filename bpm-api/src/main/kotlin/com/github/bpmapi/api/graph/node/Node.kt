package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.Connector
import com.github.bpmapi.api.graph.render.NodeRenderer
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Node(val name: String) {
    private val inputConnectors: MutableList<Connector> = ArrayList()
    private val outputConnectors: MutableList<Connector> = ArrayList()
    val inputs: List<Connector> get() = inputConnectors
    val outputs: List<Connector> get() = outputConnectors
    var id: Int = 0
        internal set

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Connector> output(
        connector: T
    ): ReadOnlyProperty<Any?, T> {
        connector.parent = this
        connector.connectorType = Connector.ConnectorType.Output
        outputConnectors.add(connector)
        return object : ReadOnlyProperty<Any?, T>, Supplier<T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = connector
            override fun get() = connector
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Connector> input(
        connector: T
    ): ReadOnlyProperty<Any?, T> {
        connector.parent = this
        connector.connectorType = Connector.ConnectorType.Input
        inputConnectors.add(connector)
        return object : ReadOnlyProperty<Any?, T>, Supplier<T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = connector
            override fun get() = connector
        }
    }

    fun isLinked(): Boolean {
        for (input in inputs) if (input.isLinked()) return true
        for (output in outputs) if (output.isLinked()) return true
        return false
    }

    fun unattach() {
        inputs.forEach(Connector::unlink)
        outputs.forEach(Connector::unlink)
    }

    /**
     * This is called from the child connector,
     */
    open fun onLink(from: Connector, to: Connector) = Unit

    /**
     * Used for rendering nodes
     */
    fun accept(nodeRenderer: NodeRenderer) = nodeRenderer.renderNode(this)

}