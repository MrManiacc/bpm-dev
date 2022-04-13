package com.github.bpmapi.api.graph.node.functions

import com.github.bpmapi.api.event.Event
import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.Pin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getDeepList
import com.github.bpmapi.util.putDeepList
import net.minecraft.nbt.CompoundTag

class FunctionNode : Node("Function") {
    val NameIn by input(VarPin("name in", Type.STRING, "unnamed"))
    val EventOut by output(EventPin("event out", TickEvent::class))
    private val parametersMap: MutableMap<String, VarPin> = HashMap()
    val parameters: Collection<VarPin> get() = parametersMap.values
    var varType: Type = Type.BOOLEAN
    var varName: String = "param0"

    fun addParameter(varPin: VarPin) {
        varPin.parent = this
        varPin.connectorType = Pin.ConnectorType.Output
        outputConnectors.add(varPin)
        parametersMap[varPin.name] = varPin
    }

    fun call(event: Event, pins: Collection<VarPin>) {
        pins.forEach {
            val target = parametersMap[it.name]
            if (target != null && target.validate(it)) target.value = it(target.value)
        }
        EventOut.callEvent(event)
    }

    fun getParameter(name: String): VarPin? = parametersMap[name]

    override fun CompoundTag.serialize() {
        put("nameIn", NameIn.serializeNBT())
        put("eventOut", EventOut.serializeNBT())
        putDeepList("parameters", parameters.toList())
    }

    override fun CompoundTag.deserialize() {
        NameIn.deserializeNBT(getCompound("nameIn"))
        EventOut.deserializeNBT(getCompound("eventOut"))
        val params = getDeepList<VarPin>("parameters")
        params.forEach(::addParameter)
    }

}