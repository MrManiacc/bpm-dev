package com.github.bpm.render

import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.node.*
import com.github.bpmapi.api.type.Type
import com.google.common.collect.Queues
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation
import imgui.flag.ImGuiMouseButton
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE
import java.util.*

class GraphRenderer(private val graph: Graph) {
    private val linkA = ImInt()
    private val linkB = ImInt()
    private var hovered = false
    private val startTime = System.currentTimeMillis()
    private val positions: Queue<Graph.PositionMeta> = Queues.newArrayDeque()

    fun render() {
        graph.iterate(Node::render)
        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight)
        hovered = ImNodes.isEditorHovered()
//        var id = 0
//        graph.inputs.forEach { pin ->
//            pin.links.forEach {
//                ImNodes.link(id++, pin.id, it.value)
//            }
//        }
        graph.iterateLinks { linkId, input, output ->
            ImNodes.link(linkId, input.id, output.id)
        }
        graph.drainMetas {
            ImNodes.setNodeScreenSpacePos(it.nodeId, it.x, it.y)
        }
    }

    /**
     * Used to update the positions in the graph
     */
    fun beforeClose() {
        graph.iterate {
            graph.addMeta(it.id, ImNodes.getNodeScreenSpacePosX(it.id), ImNodes.getNodeScreenSpacePosY(it.id))
        }
    }


    fun poll() {
        if (ImNodes.isLinkCreated(linkA, linkB)) {
            val source = graph.findByOutputId(linkA.get())
            val target = graph.findByInputId(linkB.get())
            if (source != null && target != null) {
                graph.link(target, source)
//                source.linkTo(target, nextLinkId++)
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
            if (ImGui.button("Create Function")) {
                val node = FunctionNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Call")) {
                val node = CallNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Variable")) {
                val node = VarNode(Type.BOOLEAN, false)
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }

            if (ImGui.button("Create Tick")) {
                val node = TickNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Extract")) {
                val node = ExtractNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Buffer")) {
                val node = BufferNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Insert")) {
                val node = InsertNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            if (ImGui.button("Create Block Link")) {
                val node = BlockNode()
                graph.addNode(node)
                ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY())
                ImGui.closeCurrentPopup()
            }
            ImGui.endPopup()
        }
    }

}