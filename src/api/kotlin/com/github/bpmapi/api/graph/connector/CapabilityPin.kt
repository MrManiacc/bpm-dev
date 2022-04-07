package com.github.bpmapi.api.graph.connector

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

interface CapabilityPin : IItemHandler {

    fun extractTo(inventory: IItemHandler, count: Int = 64) {
        for (i in 0 until slots) {
            val extracted = extractItem(i, count, false)
            val leftOver = ItemHandlerHelper.insertItem(inventory, extracted, false)
            insertItem(i, leftOver, false)
        }
    }

    fun extractFrom(inventory: IItemHandler, count: Int = 64) {
        for (i in 0 until inventory.slots) {
            val extracted = inventory.extractItem(i, count, false)
            val leftOver = ItemHandlerHelper.insertItem(this, extracted, false)
            inventory.insertItem(i, leftOver, false)
        }
    }

}