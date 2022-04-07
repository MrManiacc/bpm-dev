package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getEnum
import com.github.bpmapi.util.getPrimitive
import com.github.bpmapi.util.putEnum
import com.github.bpmapi.util.putPrimitive
import net.minecraft.nbt.CompoundTag


class VarPin(
    name: String = "variable", var type: Type = Type.INT, var value: Any? = null
) : Pin(name) {

    /**
     * This makes it so that only var connectors can connect to var connectors
     */
    override fun validate(other: Pin): Boolean {
        return other is VarPin && other.type == type && if (connectorType == ConnectorType.Input) !isLinked() else true
    }

    override fun CompoundTag.serialize() {
        putEnum("variable_type", this@VarPin.type)
        putPrimitive("value", value)
    }

    override fun CompoundTag.deserialize() {
        this@VarPin.type = getEnum("variable_type")
        value = getPrimitive("value")
    }

    override fun onLink(other: Pin) {
        if (connectorType == ConnectorType.Input && other is VarPin) {
            this.value = other.value
        }
    }


    inline operator fun <reified T : Any> invoke(defaultValue: T? = null): T? {
        if (connectorType == ConnectorType.Input && isLinked()) {
            val value = links<VarPin>().first().value ?: this.value
            if (T::class.isInstance(value)) return value as T
        }
        if (T::class.isInstance(value))
            return value as T
        return defaultValue
    }
}