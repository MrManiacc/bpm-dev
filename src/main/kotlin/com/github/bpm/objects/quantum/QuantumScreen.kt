package com.github.bpm.objects.quantum

import com.github.bpm.render.EditorRenderer
import com.github.bpm.render.PropertiesRenderer
import com.github.bpm.util.Gui
import com.mojang.blaze3d.vertex.PoseStack
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.ImNodesContext
import imgui.extension.nodeditor.NodeEditorContext
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class QuantumScreen(private val tile: QuantumTile) : Screen(TextComponent("Quantum Screen")) {
    private val context = NodeEditorContext()
    private val nodeContext = ImNodesContext()
    private val editorRenderer = EditorRenderer(tile.graph)
    private val propertiesRenderer = PropertiesRenderer(tile.graph)

    override fun render(stack: PoseStack, var1: Int, var2: Int, var3: Float) = Gui.frame {
        Gui.dockspace("Quantum Screen", propertiesRenderer::render, ::renderViewport)
    }

    fun renderViewport() {
        ImNodes.editorContextSet(nodeContext)
        Gui.nodeGraph("Quantum Screen", editorRenderer::render)
        editorRenderer.poll()
    }


    override fun onClose() {
        tile.pushGraph()
        context.destroy()
        nodeContext.destroy()
        super.onClose()
    }
}