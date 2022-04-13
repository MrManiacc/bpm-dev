package com.github.bpmapi.api.graph.node.items

import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.CapabilityPin
import com.github.bpmapi.api.graph.connector.StorePin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.items.IItemHandler

class InsertNode : Node("Insert") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val OutputBlock by input(StorePin("block in", 1))
    val ItemsPerTick by input(VarPin("rate in", Type.INT, 1))
    val Inventory by input(StorePin("in", 1)) //"Infinite" item output buffer
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        OutputBlock.links<CapabilityPin>().forEach {
            Inventory.extractTo(it as IItemHandler, ItemsPerTick(1)!!)
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("doAction", DoAction.serializeNBT())
        put("inputBlock", OutputBlock.serializeNBT())
        put("IPT", ItemsPerTick.serializeNBT())
        put("inv", Inventory.serializeNBT())
        put("pass", PassEvent.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("doAction"))
        OutputBlock.deserializeNBT(getCompound("inputBlock"))
        ItemsPerTick.deserializeNBT(getCompound("IPT"))
        Inventory.deserializeNBT(getCompound("inv"))
        PassEvent.deserializeNBT(getCompound("pass"))
    }
}