package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.*
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.extractTo
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.items.CapabilityItemHandler

class ExtractNode : Node("Extract") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val InputBlock by input(InventoryPin("block in", 1))
    val ItemsPerTick by input(VarPin("rate in", Type.INT, 1))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer
    val Inventory by output(InventoryPin("out", 1)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        InputBlock.links<CapabilityPin>().forEach {
            it.extractTo(Inventory, ItemsPerTick(1)!!)
        }
        Inventory.links<InventoryPin>().forEach {
            Inventory.extractTo(it, 64)
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("doAction", DoAction.serializeNBT())
        put("inputBlock", InputBlock.serializeNBT())
        put("IPT", ItemsPerTick.serializeNBT())
        put("inv", Inventory.serializeNBT())
        put("pass", PassEvent.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("doAction"))
        InputBlock.deserializeNBT(getCompound("inputBlock"))
        ItemsPerTick.deserializeNBT(getCompound("IPT"))
        Inventory.deserializeNBT(getCompound("inv"))
        PassEvent.deserializeNBT(getCompound("pass"))
    }
}