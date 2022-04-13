package com.github.bpmapi.api.graph.node.power

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.CapabilityPin
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.StorePin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.energy.IEnergyStorage

class InsertPowerNode : Node("Insert Power") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val EnergyPerTick by input(VarPin("rate in", Type.INT, 1000))
    val OutputBlock by input(StorePin("block in", 1))
    val Input by input(StorePin("in", 0, 100_000)) //"Infinite" item output buffer
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        OutputBlock.links<CapabilityPin>().forEach {
            Input.extractTo(it as IEnergyStorage, EnergyPerTick(1000)!!)
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("action", DoAction.serializeNBT())
        put("EPT", EnergyPerTick.serializeNBT())
        put("output", OutputBlock.serializeNBT())
        put("input", Input.serializeNBT())
        put("pass", PassEvent.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("action"))
        EnergyPerTick.deserializeNBT(getCompound("EPT"))
        OutputBlock.deserializeNBT(getCompound("output"))
        Input.deserializeNBT(getCompound("input"))
        PassEvent.deserializeNBT(getCompound("pass"))
    }
}