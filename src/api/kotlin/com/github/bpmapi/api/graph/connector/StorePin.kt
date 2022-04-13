package com.github.bpmapi.api.graph.connector

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.ItemStackHandler


open class StorePin(
    name: String = "itemstack",
    maxStacks: Int,
    energyCapacity: Int = 0,
    maxReceive: Int = energyCapacity,
    maxExtract: Int = energyCapacity
) : Pin(name), CapabilityPin {
    private val items = ItemStackHandler(maxStacks)
    private val energy = EnergyStorage(energyCapacity, maxReceive, maxExtract)

    init {
        items.setSize(maxStacks)
    }

    /**
     * This makes it so that only var connectors can connect to var connectors
     */
    override fun validate(other: Pin): Boolean {
        return other is CapabilityPin
    }

    override fun CompoundTag.serialize() {
        put("items", items.serializeNBT())
        put("energy", energy.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        items.deserializeNBT(getCompound("items"))
        energy.deserializeNBT(get("energy"))
    }

    override fun getSlots(): Int = items.slots

    override fun getStackInSlot(slot: Int): ItemStack = items.getStackInSlot(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        items.insertItem(slot, stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        items.extractItem(slot, amount, simulate)

    override fun getSlotLimit(slot: Int): Int = items.getSlotLimit(slot)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = items.isItemValid(slot, stack)
    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = energy.receiveEnergy(maxReceive, simulate)

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = energy.extractEnergy(maxExtract, simulate)

    override fun getEnergyStored(): Int = energy.energyStored

    override fun getMaxEnergyStored(): Int = energy.maxEnergyStored

    override fun canExtract(): Boolean = energy.canExtract()

    override fun canReceive(): Boolean = energy.canReceive()

    override fun getTanks(): Int {
        TODO("Not yet implemented")
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        TODO("Not yet implemented")
    }

    override fun getTankCapacity(tank: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        TODO("Not yet implemented")
    }

    override fun fill(resource: FluidStack?, action: IFluidHandler.FluidAction?): Int {
        TODO("Not yet implemented")
    }

    override fun drain(resource: FluidStack?, action: IFluidHandler.FluidAction?): FluidStack {
        TODO("Not yet implemented")
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction?): FluidStack {
        TODO("Not yet implemented")
    }


}