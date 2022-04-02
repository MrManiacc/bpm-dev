package com.github.bpmapi.api.graph.render

import com.github.bpmapi.api.graph.connector.Connector
import com.github.bpmapi.api.graph.connector.EventConnector
import com.github.bpmapi.api.graph.connector.VarConnector

interface ConnectorRenderer {
    fun renderConnector(connector: Connector) = when (connector) {
        is EventConnector -> renderEventConnector(connector)
        is VarConnector -> renderVarConnector(connector)
        else -> Unit//Unsupported
    }

    fun renderEventConnector(connector: EventConnector)
    fun renderVarConnector(connector: VarConnector)

}