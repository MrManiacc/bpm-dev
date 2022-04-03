package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getEnum
import com.github.bpmapi.util.putEnum
import net.minecraft.nbt.CompoundTag

class BinaryNode(var type: Type = Type.INT, var comparison: Comparison = Comparison.LessThan) : Node("Comparison") {
    val nodeA by input(VarConnector("nodeA", type))
    val nodeB by input(VarConnector("nodeB", type))
    val output by output(VarConnector("result", Type.BOOLEAN))

    override fun CompoundTag.serialize() {
        putEnum("type", this@BinaryNode.type)
        putEnum("comparison", comparison)
        put("nodeA", nodeA.serializeNBT())
        put("nodeB", nodeB.serializeNBT())
        put("result", output.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        this@BinaryNode.type = getEnum("type")
        comparison = getEnum("comparison")
        nodeA.deserializeNBT(getCompound("nodeA"))
        nodeB.deserializeNBT(getCompound("nodeB"))
        output.deserializeNBT(getCompound("result"))
    }
}

enum class Comparison(val display: String) {
    GreaterThan(">"),
    LessThan("<"),
    GreaterThanOrEqualTo(">="),
    LessThanOrEqualTo("<="),
    EqualTo("="),
    NotEqualTo("!=")
}