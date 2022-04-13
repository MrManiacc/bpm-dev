package com.github.bpmapi.api.graph.node.items

import com.github.bpmapi.api.event.FilterEvent
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.nbt.CompoundTag

class FilterNode : Node("Filter") {
    val FilterIn by input(EventPin("filter in", ::onFilter))
    val NameRegex by input(VarPin("name regex in", Type.STRING, "(.*?)"))

    private fun onFilter(event: FilterEvent) {
        val stack = event.itemStack
        if (stack == null) {
            event.accepted = false
            return
        }
        val name = stack.displayName.string.replace("[", "").replace("]", "")
        val regex = NameRegex("(.*?)")!!.toRegex()
        event.accepted = regex.matches(name)
    }

    override fun CompoundTag.serialize() {
        put("filter", FilterIn.serializeNBT())
        put("name", NameRegex.serializeNBT())
    }


    override fun CompoundTag.deserialize() {
        FilterIn.deserializeNBT(getCompound("filter"))
        NameRegex.deserializeNBT(getCompound("name"))
    }
}