package com.github.bpm.render

import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.node.BinaryNode
import com.github.bpmapi.api.graph.node.Comparison
import com.github.bpmapi.api.graph.node.TickNode
import com.github.bpmapi.api.graph.node.VarNode
import com.github.bpmapi.api.type.SystemType
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.flag.ImGuiMouseButton
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE

class EditorRenderer(private val graph: Graph) {
    private val linkA = ImInt()
    private val linkB = ImInt()
    private var hovered = false
    private val nodeRenderer = Renderer()
    private var nextLinkId = 0
    private val startTime = System.currentTimeMillis()

    fun render() {
        graph.iterate(nodeRenderer::renderNode)
        hovered = ImNodes.isEditorHovered()
        graph.outputs.forEach {
            it.links.forEach { (linkId, link) ->
                ImNodes.link(linkId, link.id, it.id)
            }
        }
    }


    fun poll() {
        if (ImNodes.isLinkCreated(linkA, linkB)) {
            val source = graph.findByOutputId(linkA.get())
            val target = graph.findByInputId(linkB.get())
            if (source != null && target != null) {
                source.linkTo(target, nextLinkId++)
            }
        }

        if (ImGui.isKeyPressed(GLFW_KEY_DELETE)) {
            val selectedNum = ImNodes.numSelectedNodes()
            if (selectedNum > 0) {
                val selected = IntArray(selectedNum) { -1 }
                ImNodes.getSelectedNodes(selected)
                selected.forEach(graph::removeNode)
            }

            val linksNum = ImNodes.numSelectedLinks()
            if (linksNum > 0) {
                val selected = IntArray(linksNum) { -1 }
                ImNodes.getSelectedLinks(selected)
                selected.forEach(graph::unlink)
            }
        }

        if (ImGui.isMouseClicked(ImGuiMouseButton.Right) && (System.currentTimeMillis() - startTime) > 100) {
            val hoverNode = ImNodes.getHoveredNode()
            if (hoverNode != -1) {
                ImGui.openPopup("node_context")
                ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), hoverNode);
            } else if (hovered) ImGui.openPopup("node_editor_context")
        }

        if (ImGui.isPopupOpen("node_context")) {
            val target = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
            if (ImGui.beginPopup("node_context")) {
                if (ImGui.button("Delete node")) {
                    graph.removeNode(target)
                    ImGui.closeCurrentPopup()
                }
                ImGui.endPopup()
            }
        }
        //Temporary
        if (ImGui.beginPopup("node_editor_context")) {
            if (ImGui.button("Create Variable")) {
                val node = VarNode(SystemType.BOOLEAN, false)
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            //Oof
            if (ImGui.button("Create Comparison")) {
                val node = BinaryNode(SystemType.INT, Comparison.EqualTo)
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create EventLoop")) {
                val node = TickNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            ImGui.endPopup()
        }
    }

}