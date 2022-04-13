package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.event.Event
import net.minecraft.nbt.CompoundTag
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.isSuperclassOf

interface NodeEvent

class EventPin<T : Event>(
    name: String = "event",
    private val type: KClass<T>,
    private val eventReceiver: (T) -> Unit = {},
) :
    Pin(name) {

    override fun validate(other: Pin): Boolean {
        return other is EventPin<*> && (type.isSuperclassOf(other.type) || type == other.type)
    }

    private fun receiveEvent(event: Any) {
        if (type.isInstance(event)) {
            eventReceiver(type.cast(event))
        }
    }

    fun callEvent(event: Any) {
        if (type.isInstance(event)) {
            val graph = parent.graph
            links.values.map { graph.findByInputId(it) }.filterIsInstance<EventPin<*>>().forEach {
                it.receiveEvent(event)
            }
        }
    }

    override fun CompoundTag.serialize() = Unit

    override fun CompoundTag.deserialize() = Unit

    companion object {
        inline operator fun <reified T : Event> invoke(
            name: String,
            noinline eventReceiver: (T) -> Unit = {}
        ): EventPin<T> = EventPin(name, T::class, eventReceiver)
    }

}