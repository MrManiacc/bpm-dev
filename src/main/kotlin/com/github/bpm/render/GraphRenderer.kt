package com.github.bpm.render

import com.github.bpm.Bpm
import com.github.bpm.objects.quantum.QuantumTile
import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.node.*
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.register.NodesRegistry
import com.google.common.collect.Queues
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.ImNodesContext
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation
import imgui.flag.ImGuiMouseButton
import imgui.type.ImInt
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE
import java.util.*

class GraphRenderer(private val tile: QuantumTile, private val context: ImNodesContext) {
    private val graph: Graph get() = tile.graph
    private val linkA = ImInt()
    private val linkB = ImInt()
    private var hovered = false
    private val startTime = System.currentTimeMillis()

    fun render() {
        graph.iterate(Node::render)
        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight)
        hovered = ImNodes.isEditorHovered()
        graph.iterateLinks { linkId, input, output ->
            ImNodes.link(linkId, input.id, output.id)
        }
        graph.takeMeta {
            ImNodes.loadEditorStateFromIniString(context, it, it.length)
        }
    }


    /**
     * Used to update the positions in the graph before pushing
     */
    fun pushUpdate() {
        graph.setMeta(ImNodes.saveEditorStateToIniString(context))
//        graph.iterate {
//            graph.addMeta(it.id, ImNodes.getNodeScreenSpacePosX(it.id), ImNodes.getNodeScreenSpacePosY(it.id))
//        }
        tile.pushGraph()
    }


    fun poll() {
        if (ImNodes.isLinkCreated(linkA, linkB)) {
            val source = graph.findByOutputId(linkA.get())
            val target = graph.findByInputId(linkB.get())
            if (source != null && target != null) {
                graph.link(target, source)
                pushUpdate()
            }
        }

        if (ImGui.isKeyPressed(GLFW_KEY_DELETE)) {
            val selectedNum = ImNodes.numSelectedNodes()
            if (selectedNum > 0) {
                val selected = IntArray(selectedNum) { -1 }
                ImNodes.getSelectedNodes(selected)
                selected.forEach(graph::removeNode)
                pushUpdate()
            }

            val linksNum = ImNodes.numSelectedLinks()
            if (linksNum > 0) {
                val selected = IntArray(linksNum) { -1 }
                ImNodes.getSelectedLinks(selected)
                selected.forEach(graph::unlink)
                pushUpdate()
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
                    pushUpdate()
                    ImGui.closeCurrentPopup()
                }
                ImGui.endPopup()
            }
        }
        //Temporary
        if (ImGui.beginPopup("node_editor_context")) {
            NodesRegistry.forEach {
                if (ImGui.button("Create ${it.name}")) {
                    val node = it.new()
                    graph.addNode(node)
                    ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                    ImGui.closeCurrentPopup()
                    pushUpdate()
                }
            }
            ImGui.endPopup()
        }
    }

}