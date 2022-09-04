@file:Suppress("unused")

package com.github.bpm

import com.github.bpm.quantum.QuantumBlock
import com.github.bpm.quantum.QuantumItem
import com.github.bpm.quantum.QuantumRenderer
import com.github.bpm.quantum.QuantumTile
import com.github.bpm.render.NodeRenderer
import com.github.bpm.util.*
import com.github.bpmapi.api.BpmMod
import com.github.bpmapi.api.graph.node.functions.CallNode
import com.github.bpmapi.api.graph.node.functions.FunctionNode
import com.github.bpmapi.api.graph.node.items.BufferNode
import com.github.bpmapi.api.graph.node.items.ExtractNode
import com.github.bpmapi.api.graph.node.items.FilterNode
import com.github.bpmapi.api.graph.node.items.InsertNode
import com.github.bpmapi.api.graph.node.power.ExtractPowerNode
import com.github.bpmapi.api.graph.node.power.InsertPowerNode
import com.github.bpmapi.api.graph.node.utilities.RedstoneNode
import com.github.bpmapi.api.graph.node.utilities.TickNode
import com.github.bpmapi.api.graph.node.world.BlockNode
import com.github.bpmapi.register.*
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.eventbus.api.IEventBus
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
        val VarNode by register(
            "Variable",
            "utilities",
            { com.github.bpmapi.api.graph.node.utilities.VarNode() },
            NodeRenderer::renderVariableNode
        )
        val TickNode by register("Tick", "utilities", { TickNode() }, NodeRenderer::renderTickNode)
        val ExtractNode by register("Extract", "items", { ExtractNode() }, NodeRenderer::renderExtract)
        val InsertNode by register("Insert", "items", { InsertNode() }, NodeRenderer::renderInsert)
        val InsertPowerNode by register("Insert", "power", { InsertPowerNode() }, NodeRenderer::renderInsertPower)
        val ExtractPowerNode by register("Extract", "power", { ExtractPowerNode() }, NodeRenderer::renderExtractPower)
        val BufferNode by register("Buffer", "utilities", { BufferNode() }, NodeRenderer::renderBufferNode)
        val BlockNode by register("Block Link", "world", { BlockNode() }, NodeRenderer::renderBlockNode)
        val FilterNode by register("Filter", "items", { FilterNode() }, NodeRenderer::renderFilterNode)
        val FunctionNode by register("Function", "functions", { FunctionNode() }, NodeRenderer::renderFunctionNode)
        val CallNode by register("Call", "functions", { CallNode() }, NodeRenderer::renderCallNode)
        val RedstoneNode by register("Redstone", "world", { RedstoneNode() }, NodeRenderer::renderRedstoneNode)

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
        val Selection by register { Selection() }

        override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
            super.register(modBus, forgeBus, modId)
            Selection.serverListener(Selections::stopSelection)
        }
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

        fun clientSetup(event: FMLClientSetupEvent) {
            ItemBlockRenderTypes.setRenderLayer(Blocks.Quantum, RenderType.cutout());
        }

        override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
            super.register(modBus, forgeBus, modId)
            whenClient(false) {
                forgeBus.addListener(Selections::tick)
                forgeBus.addListener(Selections::render)
                forgeBus.addListener(Selections::clickBlock)
                forgeBus.addListener(Selections::clickEmpty)
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