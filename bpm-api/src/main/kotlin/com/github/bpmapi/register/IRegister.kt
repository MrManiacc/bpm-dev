package com.github.bpmapi.register

import net.minecraftforge.eventbus.api.IEventBus

interface IRegister {
    fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String)
}