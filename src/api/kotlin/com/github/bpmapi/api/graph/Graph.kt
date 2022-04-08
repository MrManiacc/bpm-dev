package com.github.bpmapi.api.graph

import com.github.bpm.objects.quantum.QuantumTile
import com.github.bpmapi.api.graph.connector.Pin
import com.github.bpmapi.api.graph.node.FunctionNode
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.graph.node.TickNode
import com.github.bpmapi.util.Serial
import com.github.bpmapi.util.getDeepList
import com.github.bpmapi.util.putDeepList
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.util.INBTSerializable

/**
 * A graph is a wrapper around all of the nodes. While nodes do share their connections directly
 * via edges called "connectors", we still need to keep track of all nodes because unlike a programming
 * language where everything must always be linked up, in a node editor nodes are valid while unlinked (at least for rendering)
 */
class Graph(internal val tile: QuantumTile) : Serial {
    private val nodeMap: MutableMap<Int, Node> = HashMap()
    private val inputToNode: MutableMap<Int, Pin> = HashMap()
    private val outputToNode: MutableMap<Int, Pin> = HashMap()
    private val linkMap: MutableMap<Int, Pair<Int, Int>> = HashMap()
    private val nodePositions: MutableList<PositionMeta> = ArrayList()
    private val tickNodes: MutableSet<TickNode> = HashSet()
    private var nextNodeId = 1
    private var nextPinId = 1_000_000
    private var nextLinkId = 1
    internal val nodes: Collection<Node> get() = nodeMap.values
    internal val heads: Set<TickNode> get() = tickNodes
    internal val links: Map<Int, Pair<Int, Int>> get() = linkMap
    internal val screenSpace = ScreenSpace()
    private var meta: String? = null

    internal fun setMeta(meta: String) {
        this.meta = meta
    }

    internal inline fun takeMeta(meta: (String) -> Unit) {
        this.meta?.apply(meta)
        this.meta = null
    }

    fun addPin(pin: Pin) {
        if (pin.id == -1) pin.id = nextPinId++
        if (pin.connectorType == Pin.ConnectorType.Output) outputToNode[pin.id] = pin
        else inputToNode[pin.id] = pin
    }

    internal fun findFunction(name: String): FunctionNode? {
        for (node in nodes.filterIsInstance<FunctionNode>()) {
            if ((node.NameIn.value as String) == name) return node
        }
        return null
    }


    fun addNode(node: Node) {
        if (node is TickNode) tickNodes.add(node)
        node.graph = this
        if (node.id == -1) node.id = nextNodeId++
        node.inputs.forEach(::addPin)
        node.outputs.forEach(::addPin)
        nodeMap[node.id] = node
    }

    fun link(input: Pin, output: Pin, linkId: Int = ++nextLinkId) {
        if (!input.linkTo(output, linkId)) return
        linkMap[linkId] = input.id to output.id
        output.linkTo(input, linkId)
    }

    fun unlink(linkId: Int) {
        for (node in nodes) {
            for (input in node.inputs) {
                if (input.links.containsKey(linkId)) {
                    val other = input.links[linkId]!!
                    input.unlink(linkId)
                    val link = findByOutputId(other) ?: continue
                    link.unlink(linkId)
                    linkMap.remove(linkId)
                }
            }
        }
    }

    fun findByPin(id: Int): Pin? = findByInputId(id) ?: findByOutputId(id)
    fun findByOutputId(id: Int): Pin? = outputToNode[id]
    fun findByInputId(id: Int): Pin? = inputToNode[id]
    fun findByNodeId(id: Int): Node? = nodeMap[id]

    fun removeNode(nodeId: Int) {
        val node = nodeMap.remove(nodeId) ?: return
        if (node is TickNode) tickNodes.remove(node)
        node.unattach()
        node.inputs.forEach {
            inputToNode.remove(it.id)
        }
        node.outputs.forEach {
            outputToNode.remove(it.id)
        }
    }

    internal inline fun iterateLinks(link: (Int, Pin, Pin) -> Unit) {
        for (node in nodes) {
            for (input in node.inputs) {
                for (links in input.links) {
                    link(links.key, input, findByOutputId(links.value) ?: continue)
                }
            }
        }
    }

    internal inline fun iterate(node: (Node) -> Unit) {
        nodes.forEach(node)
    }

    internal fun addMeta(nodeId: Int, x: Float, y: Float) {
        screenSpace.metas.add(PositionMeta(nodeId, x, y))
    }

    internal fun setScreenSpaceMeta(x: Float, y: Float) {
        screenSpace.x = x
        screenSpace.y = y
    }

    override fun CompoundTag.serialize() {
        putBoolean("hasMeta", meta != null)
        if (meta != null) putString("meta", meta!!)
        putInt("nextNodeId", nextNodeId)
        putInt("nextPinId", nextPinId)
        putInt("nextLinkId", nextLinkId)
        putDeepList("nodes", nodeMap.values.toList())
        val links = CompoundTag()
        links.putIntArray("keys", this@Graph.linkMap.keys.toList())
        for ((i, value) in this@Graph.linkMap.values.withIndex()) {
            links.putInt("input_${i}", value.first)
            links.putInt("output_${i}", value.second)
        }
        put("links", links)
    }


    override fun CompoundTag.deserialize() {
        if (getBoolean("hasMeta")) meta = getString("meta")
        nextNodeId = getInt("nextNodeId")
        nextPinId = getInt("nextPinId")
        nextLinkId = getInt("nextLinkId")
        nodeMap.clear()
        tickNodes.clear()
        outputToNode.clear()
        inputToNode.clear()
        linkMap.clear()
        nodePositions.clear()
        nodePositions.addAll(getDeepList("nodePositions"))
        val nodes = getDeepList<Node>("nodes")
        nodes.forEach(::addNode)
        val links = getCompound("links")
        val keys = links.getIntArray("keys")
        keys.forEachIndexed { index, linkId ->
            val input = findByInputId(links.getInt("input_$index")) ?: return@forEachIndexed
            val output = findByOutputId(links.getInt("output_$index")) ?: return@forEachIndexed
            link(input, output, linkId)
        }

    }

    fun removePin(id: Int) {
        outputToNode.remove(id)
        inputToNode.remove(id)
    }

    data class ScreenSpace(var x: Float = 0f, var y: Float = 0f, var applied: Boolean = false) :
        INBTSerializable<CompoundTag> {
        val metas: MutableList<PositionMeta> = ArrayList()

        override fun serializeNBT(): CompoundTag {
            val tag = CompoundTag()
            tag.putFloat("x", x)
            tag.putFloat("y", y)
            tag.putDeepList("metas", metas)
            return tag
        }

        override fun deserializeNBT(nbt: CompoundTag) {
            x = nbt.getFloat("x")
            y = nbt.getFloat("y")
            nbt.getDeepList("metas", metas)
        }

    }

    data class PositionMeta(var nodeId: Int = 0, var x: Float = 0f, var y: Float = 0f) : INBTSerializable<CompoundTag> {
        override fun serializeNBT(): CompoundTag {
            val tag = CompoundTag()
            tag.putInt("nodeId", nodeId)
            tag.putFloat("nodeX", x)
            tag.putFloat("nodeY", y)
            return tag
        }

        override fun deserializeNBT(nbt: CompoundTag) {
            this.nodeId = nbt.getInt("nodeId")
            this.x = nbt.getFloat("nodeX")
            this.y = nbt.getFloat("nodeY")
        }

    }

}