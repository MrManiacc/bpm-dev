package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.type.Type
import com.github.bpmapi.util.getEnum
import com.github.bpmapi.util.getPrimitive
import com.github.bpmapi.util.putEnum
import com.github.bpmapi.util.putPrimitive
import net.minecraft.nbt.CompoundTag


class VarConnector(
    name: String = "variable", var type: Type = Type.INT, var value: Any? = null
) : Connector(name) {

    /**
     * This makes it so that only var connectors can connect to var connectors
     */
    override fun validate(other: Connector): Boolean {
        return other is VarConnector && other.type == type
    }

    override fun CompoundTag.serialize() {
        putEnum("variable_type", this@VarConnector.type)
        putBoolean("has_value", value != null)
        putPrimitive("value", value)
    }

    override fun CompoundTag.deserialize() {
        this@VarConnector.type = getEnum("variable_type")
        if (getBoolean("has_value"))
            value = getPrimitive("value")
    }
}