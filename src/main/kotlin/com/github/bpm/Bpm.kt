@file:Suppress("unused")

package com.github.bpm

import com.github.bpm.objects.quantum.QuantumBlock
import com.github.bpm.objects.quantum.QuantumItem
import com.github.bpm.objects.quantum.QuantumTile
import com.github.bpm.render.NodeRenderer
import com.github.bpm.util.*
import com.github.bpm.util.runOnRender
import com.github.bpm.util.whenClient
import com.github.bpmapi.api.BpmMod
import com.github.bpmapi.api.graph.node.InsertNode
import com.github.bpmapi.register.*
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ScreenOpenEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.registries.ForgeRegistries

@Mod("bpmmod")
object Bpm : BpmMod("bpmmod") {
    /**
     * ========================Network registry=======================
     */
    object Renderers : RenderRegistry() {
        val VarNode by renderer(NodeRenderer::renderVariableNode)
        val TickNode by renderer(NodeRenderer::renderTickNode)
        val ExtractNode by renderer(NodeRenderer::renderExtract)
        val InsertNode by renderer(NodeRenderer::renderInsert)
        val BufferNode by renderer(NodeRenderer::renderBufferNode)
        val BlockNode by renderer(NodeRenderer::renderBlockNode)
        val FunctionNode by renderer(NodeRenderer::renderFunctionNode)
        val CallNode by renderer(NodeRenderer::renderCallNode)

        val EventPin by renderer(NodeRenderer::renderEventPin)
        val VarPin by renderer(NodeRenderer::renderVarPin)
        val InvPin by renderer(NodeRenderer::renderInventoryPin)
        val LinkPin by renderer(NodeRenderer::renderLinkedPin)
    }

    /**
     * ========================Network registry=======================
     */
    object Packets : NetworkRegistry() {
        val GraphUpdate by register { GraphUpdate() }
        val GraphRequest by register { GraphRequest() }
    }

    /**
     * ========================Listener registry=======================
     */
    object Listeners : ListenerRegistry() {

        /**
         * This will create the imgui instance
         */
        fun clientOnLoadComplete(event: FMLLoadCompleteEvent) {
            whenClient(false) {
                runOnRender(Gui::init)
            }
        }
    }

    /**
     * ========================Blocks registry========================
     */
    object Blocks : Registry<Block>(ForgeRegistries.BLOCKS) {
        val Quantum by register("quantum_block") { QuantumBlock() }
    }

    /**
     * ========================Items registry========================
     */
    object Items : Registry<Item>(ForgeRegistries.ITEMS) {
        val Quantum by register("quantum_block") { QuantumItem() }
    }

    /**
     * ========================Tiles registry========================
     */
    object Tiles : Registry<BlockEntityType<*>>(ForgeRegistries.BLOCK_ENTITIES) {
        val Quantum by register("quantum_block") { tile(Blocks.Quantum) { QuantumTile(it.first, it.second) } }
    }

    /**
     * ========================Containers registry====================
     */
    object Containers : Registry<MenuType<*>>(ForgeRegistries.CONTAINERS) {

    }


}