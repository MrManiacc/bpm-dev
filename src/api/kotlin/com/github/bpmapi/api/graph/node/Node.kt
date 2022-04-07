package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.connector.Pin
import com.github.bpmapi.api.graph.render.RendererRegistry
import com.github.bpmapi.util.Serial
import com.github.bpmapi.util.putEnum
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec2
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Node(val name: String) : Serial {
    protected val inputConnectors: MutableList<Pin> = ArrayList()
    protected  val outputConnectors: MutableList<Pin> = ArrayList()
    val inputs: List<Pin> get() = inputConnectors
    val outputs: List<Pin> get() = outputConnectors
    lateinit var graph: Graph
        internal set
    var id: Int = -1
        internal set


    @Suppress("UNCHECKED_CAST")
    protected fun <T : Pin> output(
        connector: T
    ): ReadOnlyProperty<Any?, T> {
        connector.parent = this
        connector.connectorType = Pin.ConnectorType.Output
        outputConnectors.add(connector)
        return object : ReadOnlyProperty<Any?, T>, Supplier<T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = connector
            override fun get() = connector
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Pin> input(
        connector: T
    ): ReadOnlyProperty<Any?, T> {
        connector.parent = this
        connector.connectorType = Pin.ConnectorType.Input
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
        inputs.forEach(Pin::unlink)
        outputs.forEach(Pin::unlink)
    }

    override fun serializeNBT(): CompoundTag =
        with(super.serializeNBT()) {
            putInt("nodeId", this@Node.id)
            this
        }

    override fun deserializeNBT(nbt: CompoundTag) {
        super.deserializeNBT(nbt)
        with(nbt) {
            this@Node.id = getInt("nodeId")
        }
    }

    /**
     * This is called from the child connector,
     */
    open fun onLink(from: Pin, to: Pin) = Unit

    /**
     * Used for rendering nodes
     */
    fun render() = RendererRegistry.render(this)

}