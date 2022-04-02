package com.github.bpmapi.api.graph.node

import com.github.bpmapi.api.graph.connector.VarConnector
import com.github.bpmapi.api.type.SystemType
import com.github.bpmapi.api.type.Type

class BinaryNode(var type: Type, var comparison: Comparison) : Node("Comparison") {
    val nodeA by input(VarConnector("nodeA", type))
    val nodeB by input(VarConnector("nodeB", type))
    val output by output(VarConnector("output", SystemType.BOOLEAN))
}

enum class Comparison(val display: String) {
    GreaterThan(">"),
    LessThan("<"),
    GreaterThanOrEqualTo(">="),
    LessThanOrEqualTo("<="),
    EqualTo("="),
    NotEqualTo("!=")
}