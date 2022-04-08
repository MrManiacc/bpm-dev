@file:Suppress("unused")

package com.github.bpm

import com.github.bpm.objects.quantum.QuantumBlock
import com.github.bpm.objects.quantum.QuantumItem
import com.github.bpm.objects.quantum.QuantumRenderer
import com.github.bpm.objects.quantum.QuantumTile
import com.github.bpm.render.NodeRenderer
import com.github.bpm.util.*
import com.github.bpmapi.api.BpmMod
import com.github.bpmapi.api.graph.node.*
import com.github.bpmapi.register.*
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.registries.ForgeRegistries

@Mod("bpmmod")
object Bpm : BpmMod("bpmmod") {
    /**
     * ========================Network registry=======================
     */
    object Renderers : NodeRegistry() {
        val VarNode by register("Variable", { VarNode() }, NodeRenderer::renderVariableNode)
        val TickNode by register("Tick", { TickNode() }, NodeRenderer::renderTickNode)
        val ExtractNode by register("Extract", { ExtractNode() }, NodeRenderer::renderExtract)
        val InsertNode by register("Insert", { InsertNode() }, NodeRenderer::renderInsert)
        val BufferNode by register("Buffer", { BufferNode() }, NodeRenderer::renderBufferNode)
        val BlockNode by register("Block", { BlockNode() }, NodeRenderer::renderBlockNode)
        val FunctionNode by register("Function", { FunctionNode() }, NodeRenderer::renderFunctionNode)
        val CallNode by register("Call", { CallNode() }, NodeRenderer::renderCallNode)

        val EventPin by register(NodeRenderer::renderEventPin)
        val VarPin by register(NodeRenderer::renderVarPin)
        val InvPin by register(NodeRenderer::renderInventoryPin)
        val LinkPin by register(NodeRenderer::renderLinkedPin)
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

        fun clientRegisterRenderers(event: RegisterRenderers) {
            event.registerBlockEntityRenderer(Tiles.Quantum) { QuantumRenderer(it) }
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