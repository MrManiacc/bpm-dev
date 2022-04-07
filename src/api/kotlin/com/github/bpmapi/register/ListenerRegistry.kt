package com.github.bpmapi.register

import com.github.bpmapi.util.info
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.IModBusEvent
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.Consumer
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*

abstract class ListenerRegistry : IRegister {
    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
        for (member in this::class.declaredFunctions) {
            if (member.visibility != KVisibility.PUBLIC) continue
            val dist =
                if (member.name.startsWith("client")) Dist.CLIENT else if (member.name.startsWith("server")) Dist.DEDICATED_SERVER else null
            if (dist != null && dist != FMLEnvironment.dist) continue
            for (param in member.parameters) {
                val type = param.type.classifier as KClass<*>
                if (type.isSubclassOf(Event::class)) {
                    val modType: Class<out Event> = type.java as Class<out Event>
                    if (type.isSubclassOf(IModBusEvent::class))
                        modBus.addListener(EventPriority.LOWEST, true, modType) {
                            member.call(this, it)
                        }
                    else
                        forgeBus.addListener(EventPriority.LOWEST, true, modType) {
                            member.call(this, it)
                        }
                    info { "Found mod bus event named '${member.name}', with event type '${modType.simpleName}'" }
                    continue
                }
            }
        }
    }

}