@file:Suppress("unused")

package com.github.bpm

import com.github.bpm.objects.quantum.QuantumBlock
import com.github.bpm.objects.quantum.QuantumItem
import com.github.bpm.objects.quantum.QuantumTile
import com.github.bpm.util.*
import com.github.bpm.util.runOnRender
import com.github.bpm.util.whenClient
import com.github.bpmapi.net.NetworkRegistry
import com.github.bpmapi.register.BpmMod
import com.github.bpmapi.register.ListenerRegistry
import com.github.bpmapi.register.Registry
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.registries.ForgeRegistries

@Mod("bpmmod")
object Bpm : BpmMod("bpmmod") {

    /**
     * ========================Network registry=======================
     */
    object Network : NetworkRegistry() {
        val SYNC_REQUEST by register { GraphRequest() }
        val SYNC_RESPONSE by register { GraphResponse() }
    }

    /**
     * ========================Listener registry=======================
     */
    object Listeners : ListenerRegistry() {
        /**
         * This will create the imgui instance
         */
       fun onLoadComplete(event: FMLLoadCompleteEvent) {
            whenClient(logical = false) {
                runOnRender(Gui::init)
            }
        }
    }

    /**
     * ========================Blocks registry========================
     */
    object Blocks : Registry<Block>(ForgeRegistries.BLOCKS) {
        val QUANTUM by register("quantum_block") { QuantumBlock() }
    }

    /**
     * ========================Items registry========================
     */
    object Items : Registry<Item>(ForgeRegistries.ITEMS) {
        val QUANTUM by register("quantum_block") { QuantumItem() }
    }

    /**
     * ========================Tiles registry========================
     */
    object Tiles : Registry<BlockEntityType<*>>(ForgeRegistries.BLOCK_ENTITIES) {
        val QUANTUM by register("quantum_block") { tile(Blocks.QUANTUM) { QuantumTile(it.first, it.second) } }
    }

    /**
     * ========================Containers registry====================
     */
    object Containers : Registry<MenuType<*>>(ForgeRegistries.CONTAINERS) {

    }


}