package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.InventoryPin
import com.github.bpmapi.util.extractTo
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.items.CapabilityItemHandler

class BufferNode : Node("Buffer") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val InventoryIn by input(InventoryPin("in", 1))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer
    val InventoryOut by output(InventoryPin("out", 4600))

    private fun onEvent(actionEvent: TickEvent) {
        InventoryIn.extractTo(InventoryOut, 64)
        if (InventoryOut.isLinked()) {
            InventoryOut.links<InventoryPin>().forEach {
                InventoryOut.extractTo(it, 64)
            }
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("doAction", DoAction.serializeNBT())
        put("invIn", InventoryIn.serializeNBT())
        put("invOut", InventoryOut.serializeNBT())
        put("pass", PassEvent.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("doAction"))
        InventoryIn.deserializeNBT(getCompound("invIn"))
        InventoryOut.deserializeNBT(getCompound("invOut"))
        PassEvent.deserializeNBT(getCompound("pass"))
    }
}