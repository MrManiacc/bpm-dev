package com.github.bpm.render

import com.github.bpm.util.drawValue
import com.github.bpm.util.info
import com.github.bpmapi.api.graph.connector.EventConnector
import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.graph.node.*
import com.github.bpmapi.api.graph.render.ConnectorRenderer
import com.github.bpmapi.api.graph.render.NodeRenderer
import com.github.bpmapi.api.type.Type
import imgui.ImColor
import imgui.ImGui
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesColorStyle
import imgui.type.ImInt

class Renderer : NodeRenderer, ConnectorRenderer {
    private val types = Type.values().map { it.name }.toTypedArray()
    private val comparisons = Comparison.values().map { it.display }.toTypedArray()
    private val selected = ImInt(0)


    override fun renderVarNode(node: VarNode) {
        ImNodes.pushColorStyle(ImNodesColorStyle.TitleBar, ImColor.intToColor(166, 47, 222))
        ImNodes.pushColorStyle(ImNodesColorStyle.TitleBarHovered, ImColor.intToColor(149, 20, 210))
        ImNodes.pushColorStyle(ImNodesColorStyle.TitleBarSelected, ImColor.intToColor(153, 10, 220))
        begin(node)
        selected.set(node.type.ordinal)
        ImGui.pushItemWidth(80f)
        if (ImGui.combo("type##${node.id}", selected, types)) {
            node.type = Type.values()[selected.get()]
            node.output.type = node.type
            node.unattach()//must detach all links when updating type
            info { "Updated node type to ${node.type.typeName}" }
        }
        node.output.drawValue()
        ImGui.popItemWidth()
        end(node)
        ImNodes.popColorStyle()
        ImNodes.popColorStyle()
        ImNodes.popColorStyle()
    }

    override fun renderCompareNode(node: BinaryNode) {
        begin(node)
        selected.set(node.type.ordinal)
        ImGui.pushItemWidth(80f)
        if (ImGui.combo("type##${node.id}", selected, types)) {
            node.type = Type.values()[selected.get()]
            node.nodeA.type = node.type
            node.nodeB.type = node.type
            node.unattach()//must detach all links when updating type
            info { "Updated node type to ${node.type.typeName}" }
        }
        ImGui.pushItemWidth(45f)
        node.nodeA.drawValue("A", 0f)
        selected.set(node.comparison.ordinal)
        ImGui.sameLine()
        if (ImGui.combo("##${node.id}", selected, comparisons)) {
            node.comparison = Comparison.values()[selected.get()]
        }
        ImGui.sameLine()
        node.nodeB.drawValue("B", 0f)
        ImGui.popItemWidth()
        end(node)
    }

    override fun renderTickNode(node: TickNode) {
        begin(node)
        ImGui.pushItemWidth(80f)
        node.rate.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    override fun renderEventConnector(connector: EventConnector) {
        ImGui.text(connector.name)
    }

    override fun renderVarConnector(connector: VarConnector) {
        ImGui.text(connector.name)
    }

    private fun begin(node: Node) {
        ImNodes.beginNode(node.id)
        ImNodes.beginNodeTitleBar()
        ImGui.text(node.name)
        ImNodes.endNodeTitleBar()
    }


    private fun end(node: Node) {
        val size = if (node.inputs.size > node.outputs.size) node.inputs.size else node.outputs.size
        for (i in 0 until size) {
            if (i < node.inputs.size) {
                val input = node.inputs[i]
                ImNodes.beginInputAttribute(input.id)
                input.accept(this)
                ImNodes.endInputAttribute()
                if (i < node.outputs.size) {
                    ImGui.sameLine()
                    ImGui.dummy(75f, 0f)
                    ImGui.sameLine()
                    val output = node.outputs[i]
                    ImNodes.beginOutputAttribute(output.id)
                    output.accept(this)
                    ImNodes.endOutputAttribute()
                }
            } else {
                val output = node.outputs[i]
                ImNodes.beginOutputAttribute(output.id)
                ImGui.dummy(75f, 0f)
                ImGui.sameLine()
                output.accept(this)
                ImNodes.endOutputAttribute()
            }
        }
        ImNodes.endNode()
    }

}