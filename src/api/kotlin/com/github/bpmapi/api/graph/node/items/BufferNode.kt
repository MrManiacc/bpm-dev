package com.github.bpmapi.api.graph.node.items

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.StorePin
import com.github.bpmapi.api.graph.node.Node
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.items.IItemHandler

class BufferNode : Node("Buffer") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val InventoryIn by input(StorePin("in", 1, 10_000_000))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer
    val InventoryOut by output(StorePin("out", 4600, 999_999_999))

    private fun onEvent(actionEvent: TickEvent) {
        InventoryIn.extractTo(InventoryOut as IItemHandler, 64)
        InventoryIn.extractTo(InventoryOut as IEnergyStorage, InventoryIn.energyStored)
        if (InventoryOut.isLinked()) {
            InventoryOut.links<StorePin>().forEach {
                InventoryOut.extractTo(it as IItemHandler, 64)
                InventoryOut.extractTo(it as IEnergyStorage, 1_000_000)
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