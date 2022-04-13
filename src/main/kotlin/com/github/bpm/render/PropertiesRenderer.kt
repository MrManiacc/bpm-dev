package com.github.bpm.render

import com.github.bpm.util.drawValue
import com.github.bpmapi.api.graph.Graph
import com.github.bpmapi.api.graph.connector.StorePin
import com.github.bpmapi.api.graph.connector.VarPin
import imgui.ImGui
import imgui.extension.imnodes.ImNodes

class PropertiesRenderer(private val graph: Graph) {

    fun render() {
        ImGui.text("Node count: ${graph.nodes.size}")
        ImGui.text("Links: ${graph.nodes.map { it -> it.outputs.map { it.links.size } }}")
        ImGui.separator()
        ImGui.newLine()
        ImGui.text("Nodes: ")
        val count = ImNodes.numSelectedNodes()
        if (count > 0) {
            val selected = IntArray(count)
            ImNodes.getSelectedNodes(selected)
            for (nodeId in selected) {
                val node = graph.findByNodeId(nodeId) ?: continue
                if (ImGui.treeNode("${node.name}##${node.id}")) {
                    if (ImGui.button("focus"))
                        ImNodes.editorMoveToNode(node.id)
                    if (node.inputs.isNotEmpty()) {
                        ImGui.text("Inputs")
                        node.inputs.filterIsInstance<VarPin>().forEach { it.drawValue(padding = 0f) }
                        node.inputs.filterIsInstance<StorePin>().forEach { it.drawValue() }
                    }
                    if (node.outputs.isNotEmpty()) {
                        ImGui.text("Outputs")
                        node.outputs.filterIsInstance<VarPin>().forEach { it.drawValue(padding = 0f) }
                        node.outputs.filterIsInstance<StorePin>().forEach { it.drawValue() }
                    }
                    ImGui.treePop()
                    ImGui.separator()
                }
            }
        }

    }
}