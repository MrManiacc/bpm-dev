package com.github.bpmapi.api.graph.connector

import net.minecraft.nbt.CompoundTag
import net.minecraftforge.eventbus.api.Event

class EventConnector(name: String = "event") : Connector(name) {

    override fun validate(other: Connector): Boolean {
        return other is EventConnector
    }

    override fun CompoundTag.serialize() {
    }

    override fun CompoundTag.deserialize() {
    }

}