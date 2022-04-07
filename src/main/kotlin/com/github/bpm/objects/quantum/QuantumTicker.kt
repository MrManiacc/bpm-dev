package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpm.Bpm.Packets.sendToClient
import com.github.bpm.util.*
import com.github.bpm.util.info
import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.util.getLevel
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkEvent.Context
import java.util.UUID
import kotlin.math.abs

object QuantumTicker : BlockEntityTicker<QuantumTile> {
    private val tickEvent = TickEvent()
    private val storedPositions = HashMap<UUID, Vec3>()

    /**
     * Used to update the quantum tiles
     */
    override fun tick(world: Level, pos: BlockPos, state: BlockState, tile: QuantumTile) = with(tile.graph.heads) {
        if (!world.isClientSide) {
            tickEvent.owner = tile
            forEach {
                if (++it.currentTick >= abs(it.Rate.value as Int)) {
                    tickEvent.sender = it.OnTick
                    it.OnTick.callEvent(tickEvent)
                    it.currentTick = 0
                }
            }
        }
    }


    fun syncServer(packet: GraphUpdate, context: NetworkEvent.Context): Boolean {
        val world = getLevel(packet.world)
        val graph = packet.graph
        val entity = world.getBlockEntity(packet.blockPos) ?: return false
        if (entity !is QuantumTile) return false
        entity.graph.deserializeNBT(graph)
        info { "Just got a graph update from client: $packet $packet" }
        Bpm.Packets.sendToClientsWithBlockLoaded(packet, packet.blockPos, world)
        entity.setChanged()
        return true
    }


    fun requestGraph(packet: GraphRequest, context: NetworkEvent.Context): Boolean {
        val world = getLevel(packet.world)
        val entity = world.getBlockEntity(packet.blockPos) ?: return false
        if (entity !is QuantumTile) return false
        val graph = entity.graph
        context.sendToClient(Bpm.Packets.GraphUpdate {
            this.world = packet.world
            this.graph = graph.serializeNBT()
            this.blockPos = packet.blockPos
        })
        return true
    }

    fun syncClient(packet: GraphUpdate, context: NetworkEvent.Context): Boolean {
        val world = Minecraft.getInstance().level ?: return false
        val graph = packet.graph
        val entity = world.getBlockEntity(packet.blockPos) ?: return false
        if (entity !is QuantumTile) return false
        entity.graph.deserializeNBT(graph)
        info { "Just got a graph update from server: $packet $packet" }
        return true
    }


    init {
        Bpm.Packets.GraphRequest.serverListener(QuantumTicker::requestGraph)
        Bpm.Packets.GraphUpdate.serverListener(QuantumTicker::syncServer)
        Bpm.Packets.GraphUpdate.clientListener(QuantumTicker::syncClient)
    }
}