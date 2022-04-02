package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import com.github.bpmapi.api.graph.Graph
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

/**
 * This does all the dirty work of our tile entity
 */
class QuantumTile(pos: BlockPos, state: BlockState) : BlockEntity(Bpm.Tiles.QUANTUM, pos, state) {
    val graph: Graph = Graph()
}