package com.github.bpm.objects.quantum

import com.github.bpm.render.GraphRenderer
import com.github.bpm.render.PropertiesRenderer
import com.github.bpm.util.Gui
import com.mojang.blaze3d.vertex.PoseStack
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.ImNodesContext
import imgui.extension.nodeditor.NodeEditorContext
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class QuantumScreen(val tile: QuantumTile) : Screen(TextComponent("Quantum Screen")) {
    private val context = NodeEditorContext()
    private val nodeContext = ImNodesContext()
    private val propertiesRenderer = PropertiesRenderer(tile.graph)
    private val graphRenderer = GraphRenderer(tile.graph)
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
        Gui.nodeGraph("Quantum Screen", graphRenderer::render)
        graphRenderer.poll()
    }

    fun close() {
        if (!closed) {
            graphRenderer.beforeClose()
            tile.pushGraph()
            context.destroy()
            nodeContext.destroy()
            closed = true
        }
    }

    override fun onClose() {
        close()
        super.onClose()
    }
}