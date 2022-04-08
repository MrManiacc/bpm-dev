package com.github.bpmapi.register

import com.github.bpmapi.api.graph.connector.Pin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.graph.node.NodeWrapper
import com.github.bpmapi.api.graph.render.Renderer
import net.minecraftforge.eventbus.api.IEventBus
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class NodeRegistry : RenderRegistry() {
    protected val nodes: MutableMap<KClass<out Node>, NodeWrapper<*>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T : Node> register(
        nodeName: String,
        noinline nodeSupplier: () -> T,
        noinline nodeRenderer: (T) -> Unit
    ): ReadOnlyProperty<Any?, NodeWrapper<T>> {
        val renderer: Renderer<T> = Renderer { nodeRenderer(it) }
        val nodeWrapper = NodeWrapper(nodeName, nodeSupplier, renderer)
        nodes[T::class] = nodeWrapper
        renderers[T::class] = renderer
        return object : ReadOnlyProperty<Any?, NodeWrapper<T>>, Supplier<NodeWrapper<T>> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
            override fun get() = nodeWrapper
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Node> getNode(type: KClass<T>): NodeWrapper<T> {
        val node = nodes[type] ?: error("Attempted to retrieve invalid node ${type.qualifiedName}")
        return node as NodeWrapper<T>
    }

    fun forEach(consumer: (NodeWrapper<*>) -> Unit) {
        nodes.values.forEach(consumer)
    }


    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T : Pin> register(
        noinline pinRenderer: (T) -> Unit
    ): ReadOnlyProperty<Any?, Renderer<T>> {
        val renderer: Renderer<T> = Renderer { pinRenderer(it) }
        renderers[T::class] = renderer
        return object : ReadOnlyProperty<Any?, Renderer<T>>, Supplier<Renderer<T>> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
            override fun get() = renderer
        }
    }

    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
        super.register(modBus, forgeBus, modId)
        NodesRegistry.nodes.putAll(nodes)
    }
}