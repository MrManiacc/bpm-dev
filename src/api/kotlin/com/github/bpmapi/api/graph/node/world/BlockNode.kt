package com.github.bpmapi.api.graph.node.world

import com.github.bpmapi.api.graph.connector.LinkedPin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.ISelectable
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag

class BlockNode : Node("Block Link"), ISelectable {
    val Position by input(VarPin("pos", Type.BLOCK_POS, BlockPos.ZERO))
    val Face by input(VarPin("face", Type.BLOCK_FACE, Direction.NORTH))
    val Link by output(LinkedPin("block out", Position, Face))

    val BlockName: String
        get() {
            val level = graph.tile.level ?: return "air"
            val block = level.getBlockState(Position(BlockPos.ZERO)!!)
            return block.block.name.string
        }

    override fun CompoundTag.serialize() {
        put("position", Position.serializeNBT())
        put("face", Face.serializeNBT())
        put("link", Link.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        Position.deserializeNBT(getCompound("position"))
        Face.deserializeNBT(getCompound("face"))
        Link.deserializeNBT(getCompound("link"))
    }

    override fun finishSelection(block: BlockPos, face: Direction) {
        Position.value = block
        Face.value = face
    }

}