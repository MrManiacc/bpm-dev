package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpm.util.GraphResponse
import com.github.bpm.util.info
import com.github.bpm.util.runOnServer
import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.tile.BpmTile
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.network.NetworkEvent

/**
 * This does all the dirty work of our tile entity
 */
class QuantumTile(pos: BlockPos, state: BlockState) : BpmTile(Bpm.Tiles.QUANTUM, pos, state) {
    var graph: Graph = Graph()
        private set

    init {
        Bpm.Network.SYNC_RESPONSE.serverListener(::onClientSync)
    }


    private fun onClientSync(packet: GraphResponse, context: NetworkEvent.Context): Boolean {
        println("test")
        if (level?.isClientSide == false) {
            runOnServer { //thread saftey?
                graph = packet.graph
                update()
            }
        }

        return true
    }

    fun pushGraph() {
        if (level?.isClientSide == true) {
            val packet = Bpm.Network.SYNC_RESPONSE {
                graph = this@QuantumTile.graph
                blockPos = this@QuantumTile.worldPosition
            }
            Bpm.Network.sendToServer(packet)
        }
    }

    override fun saveTag(tag: CompoundTag) {
        tag.put("graph", graph.serializeNBT())
    }

    override fun loadTag(tag: CompoundTag) {
        graph.deserializeNBT(tag.getCompound("graph"))
    }


}