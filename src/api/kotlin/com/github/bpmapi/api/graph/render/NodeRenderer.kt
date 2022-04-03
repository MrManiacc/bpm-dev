package com.github.bpmapi.api.graph.render

import com.github.bpmapi.api.graph.node.BinaryNode
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.graph.node.TickNode
import com.github.bpmapi.api.graph.node.VarNode

interface NodeRenderer {
    fun renderNode(node: Node) = when (node) {
        is VarNode -> renderVarNode(node)
        is BinaryNode -> renderCompareNode(node)
        is TickNode -> renderTickNode(node)
        else -> Unit //Unsupported
    }
    fun renderVarNode(node: VarNode)
    fun renderCompareNode(node: BinaryNode)
    fun renderTickNode(node: TickNode)
}