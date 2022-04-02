package com.github.bpmapi.api.graph

import com.github.bpmapi.api.graph.connector.Connector
import com.github.bpmapi.api.graph.node.Node

/**
 * A graph is a wrapper around all of the nodes. While nodes do share their connections directly
 * via edges called "connectors", we still need to keep track of all nodes because unlike a programming
 * language where everything must always be linked up, in a node editor nodes are valid while unlinked (at least for rendering)
 */
class Graph {
    private val nodeMap: MutableMap<Int, Node> = HashMap()
    private val inputToNode: MutableMap<Int, Connector> = HashMap()
    private val outputToNode: MutableMap<Int, Connector> = HashMap()
    private var nextNodeId = 1
    private var nextPinId = 1_000_000
    val nodes: Collection<Node> get() = nodeMap.values
    val inputs: Collection<Connector> get() = inputToNode.values
    val outputs: Collection<Connector> get() = outputToNode.values

    fun addNode(node: Node) {
        node.id = nextNodeId++
        node.inputs.forEach {
            it.id = nextPinId++
            inputToNode[it.id] = it
        }
        node.outputs.forEach {
            it.id = nextPinId++
            outputToNode[it.id] = it
        }
        nodeMap[node.id] = node
    }


    fun unlink(linkId: Int) {
        outputs.forEach {
            it.unlink(linkId)
        }
    }


    fun findByOutputId(id: Int): Connector? = outputToNode[id]
    fun findByInputId(id: Int): Connector? = inputToNode[id]
    fun findByNodeId(id: Int): Node? = nodeMap[id]
    fun removeNode(nodeId: Int) {
        nodeMap.remove(nodeId)?.unattach()
    }

    inline fun iterate(node: (Node) -> Unit) {
        nodes.forEach(node)
    }

}