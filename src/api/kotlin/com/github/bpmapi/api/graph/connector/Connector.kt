package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.graph.render.ConnectorRenderer
import com.github.bpmapi.util.*
import net.minecraft.nbt.CompoundTag

abstract class Connector(var name: String) : Serial {
    private var linksMap: MutableMap<Int, Connector> = HashMap()
    val links: Map<Int, Connector> get() = linksMap
    lateinit var parent: Node
        internal set
    var id: Int = 0
        internal set
    lateinit var connectorType: ConnectorType
        internal set

    /**
     * True if we are linked to another node
     */
    fun isLinked(): Boolean = linksMap.isNotEmpty()

    /**
     * This will validate the connection between the nodes
     */
    fun linkTo(other: Connector, linkId: Int): Boolean {
        if (validate(other)) {
            linksMap[linkId] = other
            other.linksMap[linkId] = this
            return true
        }
        return false
    }

    /**
     * Removes our link regardless of whether or not it's present
     */
    fun unlink() {
        linksMap.forEach { (id, conn) -> conn.linksMap.remove(id) }
        linksMap.clear()
    }

    /**
     * Removes our link regardless of whether or not it's present
     */
    fun unlink(linkId: Int) {
        linksMap.values.forEach { it.linksMap.remove(linkId) }
        linksMap.remove(linkId)
    }

    override fun serializeNBT(): CompoundTag =
        with(super.serializeNBT()) {
            putString("name", name)
            putInt("connector_id", this@Connector.id)
            putEnum("connector_type", connectorType)
            val links = CompoundTag()
            links.putIntArray("linkIds", linksMap.keys.toList())
            links.putDeepList("connectors", linksMap.values.filterNot { it == this@Connector }.toList())
            put("links", links)
            this
        }


    override fun deserializeNBT(nbt: CompoundTag) {
        super.deserializeNBT(nbt)
        with(nbt) {
            name = getString("name")
            this@Connector.id = getInt("connector_id")
            connectorType = getEnum("connector_type")
            unlink()
            val links = getCompound("links")
            val keys = links.getIntArray("linkIds")
            val values = links.getDeepList<Connector>("connectors")
            assert(keys.size == values.size)
            keys.forEachIndexed { index, linkId ->
                val connector = values[index]
                linkTo(connector, linkId)
            }
        }
    }


    /**
     * Checks to see if the given link is valid for this connector
     */
    abstract fun validate(other: Connector): Boolean

    fun accept(connectorRenderer: ConnectorRenderer) = connectorRenderer.renderConnector(this)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Connector) return false

        if (name != other.name) return false
        if (parent != other.parent) return false
        if (id != other.id) return false
        if (connectorType != other.connectorType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + parent.hashCode()
        result = 31 * result + id
        result = 31 * result + connectorType.hashCode()
        return result
    }


    enum class ConnectorType {
        Input,
        Output
    }


}