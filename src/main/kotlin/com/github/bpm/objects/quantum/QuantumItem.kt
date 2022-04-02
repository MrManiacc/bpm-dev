package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab

/**
 * Our item that will be displayed in game
 */
class QuantumItem : BlockItem(
    Bpm.Blocks.QUANTUM,
    Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).fireResistant()
)