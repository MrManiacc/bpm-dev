package com.github.bpmapi.api.event

import com.github.bpmapi.api.graph.connector.Pin
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

class TickEvent(var sender: Pin? = null, var owner: BlockEntity? = null) : Event()
class FilterEvent(var itemStack: ItemStack? = null, var accepted: Boolean = false) : Event()