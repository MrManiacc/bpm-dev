package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.EventConnector
import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag

class TickNode : Node("Tick") {
    val rate by input(VarConnector("rate", Type.INT, 20))
    val onTick by output(EventConnector("onTick"))

    override fun CompoundTag.serialize() {
        put("rate", rate.serializeNBT())
        put("onTick", onTick.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        rate.deserializeNBT(getCompound("rate"))
        onTick.deserializeNBT(getCompound("onTick"))
    }

}