package com.github.bpmapi.api.render

import com.github.bpm.render.NodeRenderer
import com.github.bpmapi.api.graph.connector.Pin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imnodes.ImNodes
import imgui.type.ImInt
import imgui.type.ImString

class RenderContext {
    val selected = ImInt(0)
    val bufferText = ImString()
    val bufferVec = ImVec2()
    val types = Type.values().map { it.name }.toTypedArray()

    fun begin(node: Node, size: Int = -1) {
        ImGui.calcTextSize(bufferVec, node.name)
        ImNodes.beginNode(node.id)
        ImNodes.beginNodeTitleBar()
        ImGui.text(node.name)
        if (size != -1) {
            ImGui.sameLine()
            ImGui.dummy(size - bufferVec.x, 0f)
        }
        ImNodes.endNodeTitleBar()

    }

    fun renderPin(pin: Pin) {
        val size = ImNodes.getNodeDimensionsX(pin.parent.id)
        if (pin.connectorType == Pin.ConnectorType.Output) {
            ImGui.calcTextSize(bufferVec, pin.name)
            val width = (size - bufferVec.x - 25)
            ImGui.dummy(width, 0f)
            ImGui.sameLine()
            ImGui.text(pin.name)
        } else ImGui.text(pin.name)
    }

    fun end(node: Node) {
        node.inputs.forEach {
            ImNodes.beginInputAttribute(it.id)
            it.render()
            ImNodes.endInputAttribute()
        }
        node.outputs.forEach {
            ImNodes.beginOutputAttribute(it.id)
            it.render()
            ImNodes.endOutputAttribute()
        }
        ImNodes.endNode()
    }
}