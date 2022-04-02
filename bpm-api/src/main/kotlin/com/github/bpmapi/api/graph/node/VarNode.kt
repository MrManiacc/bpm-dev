package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.Type

/**
 * A variable node is used to store a singular value, like an int, boolean, float, string, etc.
 */
class VarNode(
    var type: Type,
    value: Any? = null
) : Node("Variable") {
    val output by output(VarConnector("value", type, value))
}