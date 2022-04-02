package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpm.Bpm.Network.sendToClient
import com.github.bpm.util.SyncRequestPacket
import com.github.bpm.util.SyncResponsePacket
import com.github.bpm.util.info
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.network.NetworkEvent

object QuantumTicker : BlockEntityTicker<QuantumTile> {

    fun syncRequest(packet: SyncRequestPacket, context: NetworkEvent.Context): Boolean {
        info { "just got sync request from client: $packet" }
        context.sendToClient(Bpm.Network.SYNC_RESPONSE {
            syncBlock = packet.syncBlock.immutable()
        })
        return true
    }

    fun syncResponse(packet: SyncResponsePacket, context: NetworkEvent.Context): Boolean {
        info { "Just got a sync response from server: $packet $packet" }
        return true
    }

    /**
     * Used to update the quantum tiles
     */
    override fun tick(world: Level, pos: BlockPos, state: BlockState, tile: QuantumTile) {

    }

    init {
        Bpm.Network.SYNC_REQUEST.serverListener(QuantumTicker::syncRequest)
        Bpm.Network.SYNC_RESPONSE.clientListener(QuantumTicker::syncResponse)
    }
}