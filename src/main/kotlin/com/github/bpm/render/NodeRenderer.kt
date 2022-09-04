package com.github.bpm.render

import com.github.bpm.util.Selections
import com.github.bpm.util.drawValue
import com.github.bpm.util.info
import com.github.bpmapi.api.graph.connector.*
import com.github.bpmapi.api.graph.node.*
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
import com.github.bpmapi.api.graph.node.utilities.VarNode
import com.github.bpmapi.api.graph.node.world.BlockNode
import com.github.bpmapi.api.render.RenderContext
import com.github.bpmapi.api.type.Type
import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import imgui.extension.imnodes.ImNodes
import imgui.extension.imnodes.flag.ImNodesColorStyle
import imgui.type.ImInt
import imgui.type.ImString

object NodeRenderer {
    private val context = RenderContext()

    fun renderLinkedPin(pin: LinkedPin) = with(context) {
        renderPin(pin)
    }

    fun renderEventPin(connector: EventPin<*>) = with(context) {
//        ImGui.text(connector.name)
        renderPin(connector)
    }

    fun renderVarPin(connector: VarPin) = with(context) {
//        ImGui.text(connector.name)
        renderPin(connector)
    }

    fun renderInventoryPin(connector: StorePin) = with(context) {
//        ImGui.text(connector.name)
        renderPin(connector)
    }

    fun renderVariableNode(node: VarNode) = with(context) {
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

    fun renderBlockNode(node: BlockNode) = with(context) {
        begin(node)
        ImGui.pushItemWidth(80f)
        if (!node.Position.isLinked())
            node.Position.drawValue(drawSelectable = false)
        if (!node.Face.isLinked())
            node.Face.drawValue(drawSelectable = false)
        if (!node.Position.isLinked() && !node.Face.isLinked()) {
            if (ImGui.button("select##${node.id}")) {
                Selections.start(node)
            }
        }
        if (!node.Position.isLinked())
            ImGui.text(node.BlockName)
        ImGui.popItemWidth()
        end(node)
    }

    fun renderExtract(node: ExtractNode) = with(context) {
        begin(node, 120)
        ImGui.pushItemWidth(80f)
        if (!node.ItemsPerTick.isLinked())
            node.ItemsPerTick.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    fun renderExtractPower(node: ExtractPowerNode) = with(context) {
        begin(node, 120)
        ImGui.pushItemWidth(80f)
        if (!node.EnergyPerTick.isLinked())
            node.EnergyPerTick.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    fun renderBufferNode(node: BufferNode) = with(context) {
        begin(node, 80)
        end(node)
    }

    fun renderInsertPower(node: InsertPowerNode) = with(context) {
        begin(node, 120)
        ImGui.pushItemWidth(80f)
        if (!node.EnergyPerTick.isLinked())
            node.EnergyPerTick.drawValue()
        ImGui.popItemWidth()
        end(node)
    }


    fun renderInsert(node: InsertNode) = with(context) {
        begin(node, 120)
        ImGui.pushItemWidth(80f)
        if (!node.ItemsPerTick.isLinked())
            node.ItemsPerTick.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    fun renderRedstoneNode(node: RedstoneNode) = with(context) {
        begin(node, 80)
        //        ImGui.text(connector.name)
        ImGui.pushItemWidth(100f)
        if (!node.SignalIn.isLinked())
            node.SignalIn.drawValue()
        ImGui.popItemWidth()
        renderPin(node.SignalIn)
        renderPin(node.PowerOut)
        ImGui.pushItemWidth(80f)
        node.PowerOut.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    fun renderCallNode(node: CallNode) = with(context) {
        begin(node, 120)
        ImGui.pushItemWidth(80f)
        if (ImGui.button("set##${node.id}")) {
            val function = node.graph.findFunction(node.NameIn("unnamed")!!)
            if (function != null) {
                node.clearParameters()
                function.parameters.forEach {
                    val pin = VarPin(it.name, it.type, it.value)
                    node.addParameter(pin)
                    node.graph.addPin(pin)
                }
            }
        }
        ImGui.sameLine()
        node.NameIn.drawValue()

        ImGui.popItemWidth()
        end(node)
    }

    fun renderFilterNode(node: FilterNode) = with(context) {
        begin(node, 200)
        ImGui.pushItemWidth(80f)
        node.NameRegex.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

    fun renderFunctionNode(node: FunctionNode) = with(context) {
        begin(node, 200)
        ImGui.pushItemWidth(80f)
        node.NameIn.drawValue()
        bufferText.set(node.varName)
        if (ImGui.beginCombo("##combo_${node.id}", "add parameter")) {
            ImGui.pushItemWidth(80f)
            selected.set(node.varType.ordinal)
            if (ImGui.combo("##type_${node.id}", selected, types)) {
                node.varType = Type.values()[selected.get()]
            }
            ImGui.sameLine()
            if (ImGui.inputText("##name_${node.id}", bufferText)) {
                node.varName = bufferText.get()
            }
            ImGui.sameLine()
            if (ImGui.button("add##${node.id}")) {
                val pin = VarPin(node.varName, node.varType)
                node.addParameter(pin)
                node.graph.addPin(pin)
                node.varName = "param${node.parameters.size}"
                node.graph.tile.pushGraph()
            }
            ImGui.popItemWidth()
            ImGui.endCombo()
        }
        ImGui.popItemWidth()
        end(node)
    }

    fun renderTickNode(node: TickNode) = with(context) {
        begin(node)
        ImGui.pushItemWidth(80f)
        node.Rate.drawValue()
        node.Enabled.drawValue()
        ImGui.popItemWidth()
        end(node)
    }

}