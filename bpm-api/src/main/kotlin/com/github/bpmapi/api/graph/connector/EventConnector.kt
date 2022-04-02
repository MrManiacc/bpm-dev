package com.github.bpmapi.api.graph.connector

import net.minecraftforge.eventbus.api.Event

class EventConnector(name: String) : Connector(name) {

    override fun validate(other: Connector): Boolean {
        return other is EventConnector
    }

}