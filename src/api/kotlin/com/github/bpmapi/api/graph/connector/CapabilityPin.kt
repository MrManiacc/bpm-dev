package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.event.FilterEvent
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper

interface CapabilityPin : IItemHandler, IEnergyStorage, IFluidHandler {

    fun extractTo(inventory: IItemHandler, count: Int = 64) {
        for (i in 0 until slots) {
            val extracted = extractItem(i, count, false)
            val leftOver = ItemHandlerHelper.insertItem(inventory, extracted, false)
            insertItem(i, leftOver, false)
        }
    }

    fun extractTo(storage: IEnergyStorage, amount: Int) {
        if (canExtract() && storage.canReceive()) {
            val extracted = extractEnergy(amount, true)
            val leftOver = storage.receiveEnergy(extracted, true)
            if (leftOver != 0) storage.receiveEnergy(extractEnergy(leftOver, false), false)
        }
    }

    fun extractToFiltered(filterEventPin: EventPin<FilterEvent>, inventory: IItemHandler, count: Int = 64) {
        val event = FilterEvent(null, false)
        for (i in 0 until slots) {
            val simulated = extractItem(i, count, true)
            event.itemStack = simulated
            filterEventPin.callEvent(event)
            if (event.accepted) {
                val extracted = extractItem(i, count, false)
                val leftOver = ItemHandlerHelper.insertItem(inventory, extracted, false)
                insertItem(i, leftOver, false)
            }
        }
    }
}
