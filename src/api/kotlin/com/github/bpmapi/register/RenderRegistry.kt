package com.github.bpmapi.register

import com.github.bpmapi.api.graph.render.RendererRegistry
import com.github.bpmapi.api.graph.render.Renderer
import com.github.bpmapi.util.warn
import net.minecraftforge.eventbus.api.IEventBus
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
abstract class RenderRegistry : IRegister {
   protected val renderers: MutableMap<KClass<out Any>, Renderer<out Any>> = HashMap()

    operator fun <T : Any> get(target: KClass<T>): Renderer<T> {
        if (!renderers.containsKey(target)) error("Attempted to retrieve invalid renderer ${target.qualifiedName}")
        return renderers[target] as Renderer<T>
    }

    inline fun <reified T : Any> get(): Renderer<T> = get(T::class)

    fun <T : Any> render(target: T) {
        if (!renderers.containsKey(target::class)) {
            warn { "Failed to find renderer for type ${target::class.simpleName}" }
            return
        }
        val renderer = renderers[target::class] as Renderer<T>
        renderer.render(target)
    }


    @Suppress("UNCHECKED_CAST")
    protected inline infix fun <reified T : Any> renderer(
        crossinline supplier: (T) -> Unit
    ): ReadOnlyProperty<Any?, Renderer<T>> {
        val renderer: Renderer<T> = Renderer { supplier(it) }
        renderers[T::class] = renderer
        return object : ReadOnlyProperty<Any?, Renderer<T>>, Supplier<Renderer<T>> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = get()
            override fun get() = renderer
        }
    }

    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
        RendererRegistry.renderers.putAll(this.renderers)
    }
}