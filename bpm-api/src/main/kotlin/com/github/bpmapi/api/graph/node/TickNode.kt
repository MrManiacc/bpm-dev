package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.EventConnector
import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.SystemType

class TickNode : Node("Tick") {
    val rate by input(VarConnector("rate", SystemType.INT, 20))
    val onTick by output(EventConnector("onTick"))

}