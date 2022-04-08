package com.github.bpmapi.register

import net.minecraftforge.eventbus.api.IEventBus

object NodesRegistry : NodeRegistry() {
    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) = Unit
}