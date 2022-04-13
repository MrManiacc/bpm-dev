package com.github.bpmapi.api.graph.node.power

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.*
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.energy.IEnergyStorage

class ExtractPowerNode : Node("Extract Power") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val EnergyPerTick by input(VarPin("rate in", Type.INT, 1))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer
    val Input by input(StorePin("block in", 0, 100_000)) //"Infinite" item output buffer
    val Output by output(StorePin("out", 0, 100_000)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        Input.links<CapabilityPin>().forEach {
            it.extractTo(Output as IEnergyStorage, EnergyPerTick(1000)!!)
        }
        Output.links<CapabilityPin>().forEach {
            Output.extractTo(it as IEnergyStorage, EnergyPerTick(1000)!!)
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("event", DoAction.serializeNBT())
        put("pass", PassEvent.serializeNBT())
        put("EPT", EnergyPerTick.serializeNBT())
        put("input", Input.serializeNBT())
        put("output", Output.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("event"))
        PassEvent.deserializeNBT(getCompound("pass"))
        EnergyPerTick.deserializeNBT(getCompound("EPT"))
        Input.deserializeNBT(getCompound("input"))
        Output.deserializeNBT(getCompound("output"))
    }
}