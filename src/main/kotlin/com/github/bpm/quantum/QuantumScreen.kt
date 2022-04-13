package com.github.bpm.quantum

import com.github.bpm.render.GraphRenderer
import com.github.bpm.render.PropertiesRenderer
import com.github.bpm.util.Gui
import com.mojang.blaze3d.vertex.PoseStack
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.ImNodesContext
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class QuantumScreen(val tile: QuantumTile) : Screen(TextComponent("Quantum Screen")) {
    private val nodeContext = ImNodesContext()
    private val propertiesRenderer = PropertiesRenderer(tile.graph)
    private val graphRenderer = GraphRenderer(tile, nodeContext)
    private var closed = false

    override fun init() {
        tile.requestGraph()
        minecraft?.mouseHandler?.releaseMouse()
    }

    override fun render(stack: PoseStack, var1: Int, var2: Int, var3: Float) = Gui.frame {
        Gui.dockspace("Quantum Screen", propertiesRenderer::render, ::renderViewport)
    }

    fun renderViewport() {
        ImNodes.editorContextSet(nodeContext)
        graphRenderer.poll()
        Gui.nodeGraph("Quantum Screen", graphRenderer::render)
    }

    override fun onClose() {
        graphRenderer.pushUpdate()
        nodeContext.destroy()
        closed = true
        super.onClose()
    }
}