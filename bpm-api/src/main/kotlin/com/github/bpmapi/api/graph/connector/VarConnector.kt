package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.type.Type


class VarConnector(
    name: String, var type: Type, var value: Any? = null
) : Connector(name) {

    /**
     * This makes it so that only var connectors can connect to var connectors
     */
    override fun validate(other: Connector): Boolean {
        return other is VarConnector && other.type == type
    }
}