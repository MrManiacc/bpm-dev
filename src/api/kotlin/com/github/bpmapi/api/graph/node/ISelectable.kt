package com.github.bpmapi.api.graph.node

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

interface ISelectable {
    fun startSelection() {

    }

    fun finishSelection(block: BlockPos, face: Direction)
}