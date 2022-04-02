package com.github.bpm.util

import com.github.bpmapi.net.Packet
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf

class SyncRequestPacket : Packet() {
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

class SyncResponsePacket : Packet() {
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