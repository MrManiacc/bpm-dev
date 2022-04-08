package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.node.FunctionNode
import com.github.bpmapi.api.tile.BpmTile
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.Nameable
import net.minecraft.world.level.block.state.BlockState

/**
 * This does all the dirty work of our tile entity
 */
class QuantumTile(pos: BlockPos, state: BlockState) : BpmTile(Bpm.Tiles.Quantum, pos, state), Nameable {
    var graph: Graph = Graph(this)
        private set

    fun pushGraph() {
        if (level?.isClientSide == true) {
            val packet = Bpm.Packets.GraphUpdate {
                blockPos = this@QuantumTile.worldPosition
                graph = this@QuantumTile.graph.serializeNBT()
                world = level!!.dimension()
            }
            Bpm.Packets.sendToServer(packet)
        }
    }


    fun requestGraph() {
        if (level?.isClientSide == true) {
            val packet = Bpm.Packets.GraphRequest {
                world = level!!.dimension()
                blockPos = worldPosition.immutable()
            }
            Bpm.Packets.sendToServer(packet)
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("graph", graph.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        graph.deserializeNBT(tag.getCompound("graph"))
    }

    override fun getCustomName(): Component {
        val baseName = name.string
        val nodeCount = graph.nodes.size
        val linkCount = graph.links.size
        val functionCount = graph.nodes.filterIsInstance<FunctionNode>().size
        return TextComponent("$baseName [nodes: $nodeCount, links: $linkCount, functions: $functionCount]")
    }

    override fun getDisplayName(): Component = customName

    override fun getName(): Component {
        return blockState.block.name
    }


}