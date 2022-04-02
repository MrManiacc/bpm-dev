package com.github.bpm.util

import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.SystemType
import imgui.ImColor
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import imgui.type.ImBoolean
import imgui.type.ImDouble
import imgui.type.ImFloat
import imgui.type.ImInt
import imgui.type.ImLong
import imgui.type.ImString
import org.lwjgl.system.CallbackI.F

internal val intArrayBuffer = IntArray(1)
internal val floatArrayBuffer = FloatArray(1)
internal val stringBuffer = ImString()

internal fun VarConnector.drawValue(overrideName: String? = null, padding: Float = 38f) {
    val name = overrideName ?: this.name
    if (type !is SystemType) return
    var updated = false
    when (type) {
        SystemType.INT -> {
            intArrayBuffer[0] = if (value is Int) value as Int else 0
            if (ImGui.dragInt("$name##$id", intArrayBuffer)) {
                this.value = intArrayBuffer[0]
                info { "Updated value to $value" }
                updated = true
            }

        }
        SystemType.FLOAT -> {
            floatArrayBuffer[0] = if (value is Float) value as Float else 0f
            if (ImGui.dragFloat("$name##$id", floatArrayBuffer)) {
                this.value = floatArrayBuffer[0]
                info { "Updated value to $value" }
                updated = true
            }
        }
        SystemType.BOOLEAN -> {
            val value = if (value is Boolean) value as Boolean else false
            if (ImGui.button("$value##$id")) {
                this.value = !value
                info { "Updated value to $value" }
                updated = true
            }
            ImGui.sameLine()
            ImGui.dummy(if (value) padding else padding - 7, 0f)
            ImGui.sameLine()
            ImGui.text(name)

        }
        SystemType.STRING -> {
            stringBuffer.set(if (value is String) value else "")
            if (ImGui.inputText("$name##$id", stringBuffer)) {
                this.value = stringBuffer.get()
                info { "Updated value to $value" }
                updated = true
            }
        }
    }
    if (updated) this.links.values.filterIsInstance<VarConnector>().forEach {
        it.value = this.value
    }
}