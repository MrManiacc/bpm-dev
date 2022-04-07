package com.github.bpmapi.api.event

import com.github.bpmapi.api.graph.connector.Pin
import net.minecraft.world.level.block.entity.BlockEntity

class TickEvent(var sender: Pin? = null, var owner: BlockEntity? = null) : Event()