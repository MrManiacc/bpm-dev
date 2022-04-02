package com.github.bpmapi.register

import com.github.bpmapi.util.info
import net.minecraftforge.eventbus.api.IEventBus
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import kotlin.reflect.full.isSubclassOf

abstract class BpmMod(val id: String) {
    val ModBus: IEventBus = MOD_BUS
    val ForgeBus: IEventBus = FORGE_BUS

    init {
        currentId = id
        for (child in this::class.nestedClasses) {
            if (child.isSubclassOf(IRegister::class)) {
                val instance = child.objectInstance ?: continue
                if (instance is IRegister) {
                    instance.register(ModBus, ForgeBus, id)
                    info { "Successfully registered the '${child.simpleName}' registry" }
                }
            }
        }
    }

    internal companion object {
        internal lateinit var currentId: String
    }



}