package com.github.bpmapi.api.graph.node.utilities

import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.ISelectable
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getEnum
import com.github.bpmapi.util.putEnum
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag

/**
 * A variable node is used to store a singular value, like an int, boolean, float, string, etc.
 */
class VarNode(
    var type: Type = Type.INT,
    value: Any? = null
) : Node("Variable"), ISelectable {
    val output by output(VarPin("value", type, value))

    override fun CompoundTag.serialize() {
        putEnum("type", this@VarNode.type)
        put("value", output.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        this@VarNode.type = getEnum("type")
        output.deserializeNBT(getCompound("value"))
    }

    override fun finishSelection(block: BlockPos, face: Direction) {
        if (type == Type.BLOCK_POS)
            this.output.value = block
        else if(type == Type.BLOCK_FACE)
            this.output.value = face
    }

}