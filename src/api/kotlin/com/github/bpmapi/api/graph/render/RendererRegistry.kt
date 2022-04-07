package com.github.bpmapi.api.graph.render

import com.github.bpmapi.register.RenderRegistry
import net.minecraftforge.eventbus.api.IEventBus

object RendererRegistry : RenderRegistry(){
    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) = Unit
}