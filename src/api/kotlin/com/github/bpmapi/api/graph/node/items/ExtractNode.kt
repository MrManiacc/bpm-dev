package com.github.bpmapi.api.graph.node.items

import com.github.bpmapi.api.event.FilterEvent
import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.*
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.items.IItemHandler

class ExtractNode : Node("Extract") {
    val DoAction by input(EventPin("event in", ::onEvent))
    val InputBlock by input(StorePin("block in", 1))
    val ItemsPerTick by input(VarPin("rate in", Type.INT, 1))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer
    val Filter by output(EventPin("filter out", FilterEvent::class))
    val Inventory by output(StorePin("out", 1)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        if (!Filter.isLinked())
            Inventory.links<StorePin>().forEach { inv ->
                InputBlock.links<CapabilityPin>().forEach {
                    it.extractTo(inv as IItemHandler, ItemsPerTick(1)!!)
                }
            }
        else {
            Inventory.links<StorePin>().forEach { inv ->
                InputBlock.links<CapabilityPin>().forEach {
                    it.extractToFiltered(Filter, inv, ItemsPerTick(1)!!)
                }
            }
        }
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    override fun CompoundTag.serialize() {
        put("doAction", DoAction.serializeNBT())
        put("filter", Filter.serializeNBT())
        put("inputBlock", InputBlock.serializeNBT())
        put("IPT", ItemsPerTick.serializeNBT())
        put("inv", Inventory.serializeNBT())
        put("pass", PassEvent.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        DoAction.deserializeNBT(getCompound("doAction"))
        Filter.deserializeNBT(getCompound("filter"))
        InputBlock.deserializeNBT(getCompound("inputBlock"))
        ItemsPerTick.deserializeNBT(getCompound("IPT"))
        Inventory.deserializeNBT(getCompound("inv"))
        PassEvent.deserializeNBT(getCompound("pass"))
    }
}