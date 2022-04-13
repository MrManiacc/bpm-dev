package com.github.bpmapi.api.graph.node.functions

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.*
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getDeepList
import com.github.bpmapi.util.putDeepList
import net.minecraft.nbt.CompoundTag

class CallNode : Node("Call") {
    val EventIn by input(EventPin("event in", ::onEvent))
    val NameIn by input(VarPin("name in", Type.STRING, "unnamed"))
    val EventOut by output(EventPin("event out", TickEvent::class))
    private val parametersMap: MutableMap<String, VarPin> = HashMap()
    val parameters: Collection<VarPin> get() = parametersMap.values
    private var targetFunction: FunctionNode? = null

    fun addParameter(varPin: VarPin) {
        varPin.parent = this
        varPin.connectorType = Pin.ConnectorType.Input
        inputConnectors.add(varPin)
        parametersMap[varPin.name] = varPin
    }

    fun clearParameters() {
        parametersMap.values.forEach {
            graph.removePin(it.id)
            inputConnectors.remove(it)
        }
        parametersMap.clear()
    }

    private fun onEvent(actionEvent: TickEvent) {
        if (targetFunction == null) {
            targetFunction = graph.findFunction(NameIn("unnamed")!!)
        } else if (targetFunction!!.NameIn(null) != NameIn("unnamed")) {
            targetFunction = graph.findFunction(NameIn("unnamed")!!)
        }
        targetFunction!!.call(actionEvent, parameters)
        EventOut.callEvent(actionEvent)
    }


    override fun CompoundTag.serialize() {
        put("eventIn", EventIn.serializeNBT())
        put("nameIn", NameIn.serializeNBT())
        put("eventOut", EventOut.serializeNBT())
        putDeepList("parameters", parameters.toList())

    }

    override fun CompoundTag.deserialize() {
        EventIn.deserializeNBT(getCompound("eventIn"))
        NameIn.deserializeNBT(getCompound("nameIn"))
        EventOut.deserializeNBT(getCompound("eventOut"))
        val params = getDeepList<VarPin>("parameters")
        params.forEach(::addParameter)
    }

}