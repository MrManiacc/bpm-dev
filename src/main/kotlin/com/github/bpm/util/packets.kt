package com.github.bpm.util

import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.net.Packet
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf

class GraphResponse : Packet() {
    var graph: Graph = Graph()
    var blockPos: BlockPos = BlockPos.ZERO

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeNbt(graph.serializeNBT())
        buffer.writeBlockPos(blockPos)
    }

    override fun read(buffer: FriendlyByteBuf) {
        graph.deserializeNBT(buffer.readNbt()!!)
        this.blockPos = buffer.readBlockPos()
    }

}

class GraphRequest : Packet() {
    var syncBlock: BlockPos = BlockPos.ZERO

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeBlockPos(syncBlock)
    }

    override fun read(buffer: FriendlyByteBuf) {
        syncBlock = buffer.readBlockPos()
    }

    override fun toString(): String {
        return syncBlock.toShortString()
    }
}