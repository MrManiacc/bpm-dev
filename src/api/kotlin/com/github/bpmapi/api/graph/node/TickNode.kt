package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag

class TickNode : Node("Tick") {
    val Rate by input(VarPin("rate in", Type.INT, 20))
    val Enabled by input(VarPin("enabled in", Type.BOOLEAN, true))
    val OnTick by output(EventPin("event out", TickEvent::class))
    internal var currentTick = 0

    override fun CompoundTag.serialize() {
        put("enabled", Enabled.serializeNBT())
        put("rate", Rate.serializeNBT())
        put("onTick", OnTick.serializeNBT())
        putInt("currentTick", currentTick)
    }

    override fun CompoundTag.deserialize() {
        Enabled.deserializeNBT(getCompound("enabled"))
        Rate.deserializeNBT(getCompound("rate"))
        OnTick.deserializeNBT(getCompound("onTick"))
        currentTick = getInt("currentTick")
    }

}