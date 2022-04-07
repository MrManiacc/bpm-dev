package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.graph.render.RendererRegistry
import com.github.bpmapi.util.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level

abstract class Pin(var name: String) : Serial {
    private var linksMap: MutableMap<Int, Int> = HashMap()
    val links: Map<Int, Int> get() = linksMap
    lateinit var parent: Node
        internal set
    var id: Int = -1
        internal set
    lateinit var connectorType: ConnectorType
        internal set

    protected val world: Level?
        get() = parent.graph.tile.level

    /**
     * True if we are linked to another node
     */
    fun isLinked(): Boolean = linksMap.isNotEmpty()

    inline fun <reified T : Any> links(): List<T> =
        links.values.mapNotNull { parent.graph.findByPin(it) }.filterIsInstance<T>()

    /**
     * This will validate the connection between the nodes
     */
    fun linkTo(other: Pin, linkId: Int): Boolean {
        if (validate(other)) {
            linksMap[linkId] = other.id
            onLink(other)
            return true
        }
        return false
    }

    /**
     * Removes our link regardless of whether or not it's present
     */
    fun unlink() {
        linksMap.values.map(parent.graph::findByPin).forEach {
            if (it != null) linksMap.keys.forEach(it::unlink)
        }
        linksMap.clear()
    }

    /**
     * Removes our link regardless of whether or not it's present
     */
    fun unlink(linkId: Int) {
        linksMap.remove(linkId)
    }

    /**
     * Called when this pin is linked to another. Used for updating values of pins
     */
    protected open fun onLink(other: Pin) = Unit

    override fun serializeNBT(): CompoundTag =
        with(super.serializeNBT()) {
            putString("name", name)
            putInt("connector_id", this@Pin.id)
            putEnum("connector_type", connectorType)
            val links = CompoundTag()
            links.putIntArray("linkIds", linksMap.keys.toList())
            links.putIntArray("pinIds", linksMap.values.toList())
            put("links", links)
            this
        }


    override fun deserializeNBT(nbt: CompoundTag) {
        super.deserializeNBT(nbt)
        with(nbt) {
            name = getString("name")
            this@Pin.id = getInt("connector_id")
            connectorType = getEnum("connector_type")
            linksMap.clear()
            val links = getCompound("links")
            val keys = links.getIntArray("linkIds")
            val values = links.getIntArray("pinIds")
            assert(keys.size == values.size)
            keys.forEachIndexed { index, linkId ->
                val connector = values[index]
                linksMap[linkId] = connector
            }
        }
    }


    /**
     * Checks to see if the given link is valid for this connector
     */
    abstract fun validate(other: Pin): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pin) return false

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

    /**
     * Used for rendering connectors
     */
    fun render() = RendererRegistry.render(this)


}