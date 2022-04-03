package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpm.Bpm.Network.sendToClient
import com.github.bpm.util.GraphRequest
import com.github.bpm.util.GraphResponse
import com.github.bpm.util.info
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.network.NetworkEvent

object QuantumTicker : BlockEntityTicker<QuantumTile> {


    fun syncResponse(packet: GraphResponse, context: NetworkEvent.Context): Boolean {
        info { "Just got a sync response from server: $packet $packet" }
        return true
    }

    /**
     * Used to update the quantum tiles
     */
    override fun tick(world: Level, pos: BlockPos, state: BlockState, tile: QuantumTile) {

    }

    init {
        Bpm.Network.SYNC_RESPONSE.clientListener(QuantumTicker::syncResponse)
    }
}