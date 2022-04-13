package com.github.bpm.quantum

import com.github.bpm.Bpm
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab

/**
 * Our item that will be displayed in game
 */
class QuantumItem : BlockItem(
    Bpm.Blocks.Quantum,
    Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1).fireResistant()
)