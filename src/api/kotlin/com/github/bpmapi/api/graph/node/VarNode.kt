package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getEnum
import com.github.bpmapi.util.putEnum
import net.minecraft.nbt.CompoundTag

/**
 * A variable node is used to store a singular value, like an int, boolean, float, string, etc.
 */
class VarNode(
    var type: Type = Type.INT,
    value: Any? = null
) : Node("Variable") {
    val output by output(VarPin("value", type, value))

    override fun CompoundTag.serialize() {
        putEnum("type", this@VarNode.type)
        put("value", output.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        this@VarNode.type = getEnum("type")
        output.deserializeNBT(getCompound("value"))
    }

}