package com.github.bpmapi.api.graph.connector

import com.github.bpmapi.api.type.Type
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import kotlin.math.max

/**
 * Represents a real world tile entity
 */
class LinkedPin(
    name: String = "itemstack",
    private val position: VarPin,
    private val face: VarPin
) : Pin(name), CapabilityPin {
    private var lastPos: BlockPos? = null
    private var lastFace: Direction? = null
    private var cachedItemHandler: LazyOptional<IItemHandler> = LazyOptional.empty()
    private var cachedEnergyHandler: LazyOptional<IEnergyStorage> = LazyOptional.empty()
    private var cachedFluidHandler: LazyOptional<IFluidHandler> = LazyOptional.empty()

    @Suppress("UNCHECKED_CAST")
    /**
     * Gets the capability linked to the position/face nodes
     */
    inline fun <reified T : Any> capability(): LazyOptional<T> {
        when (T::class) {
            IItemHandler::class -> return capability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) as LazyOptional<T>
            IEnergyStorage::class -> return capability(CapabilityEnergy.ENERGY) as LazyOptional<T>
            IFluidHandler::class -> return capability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) as LazyOptional<T>
        }
        return LazyOptional.empty()
    }


    /**
     * Gets the capability linked to the position/face nodes
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> capability(type: Capability<T>): LazyOptional<T> {
        val level = world ?: return LazyOptional.empty()
        if (position.type != Type.BLOCK_POS) return LazyOptional.empty()
        val block = position<BlockPos>() ?: return LazyOptional.empty()
        if (face.type != Type.BLOCK_FACE) return LazyOptional.empty()
        val face = face<Direction>()
        when (type) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> {
                if (lastPos == block && lastFace == face && cachedItemHandler.isPresent) return cachedItemHandler as LazyOptional<T>
                val tile = level.getBlockEntity(block) ?: return LazyOptional.empty()
                lastPos = block
                lastFace = face
                val capability = if (face == null) tile.getCapability(type) else tile.getCapability(type, face)
                cachedItemHandler = capability as LazyOptional<IItemHandler>
                return capability
            }
            CapabilityEnergy.ENERGY -> {
                if (lastPos == block && lastFace == face && cachedEnergyHandler.isPresent) return cachedEnergyHandler as LazyOptional<T>
                val tile = level.getBlockEntity(block) ?: return LazyOptional.empty()
                lastPos = block
                lastFace = face
                val capability = if (face == null) tile.getCapability(type) else tile.getCapability(type, face)
                cachedEnergyHandler = capability as LazyOptional<IEnergyStorage>
                return capability
            }
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY -> {
                if (lastPos == block && lastFace == face && cachedFluidHandler.isPresent) return cachedFluidHandler as LazyOptional<T>
                val tile = level.getBlockEntity(block) ?: return LazyOptional.empty()
                lastPos = block
                lastFace = face
                val capability = if (face == null) tile.getCapability(type) else tile.getCapability(type, face)
                cachedFluidHandler = capability as LazyOptional<IFluidHandler>
                return capability
            }
        }
        return LazyOptional.empty()
    }

    /**
     * This makes it so that only var connectors can connect to var connectors
     */
    override fun validate(other: Pin): Boolean {
        return other is CapabilityPin
    }

    //Serialization not required because we're linking to virtual in world objects dynamically
    override fun CompoundTag.serialize() {
    }

    override fun CompoundTag.deserialize() {}

    override fun getSlots(): Int {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().slots
        return 0
    }


    override fun getStackInSlot(slot: Int): ItemStack {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().getStackInSlot(slot)
        return ItemStack.EMPTY
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().insertItem(slot, stack, simulate)
        return ItemStack.EMPTY
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().extractItem(slot, amount, simulate)
        return ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().getSlotLimit(slot)
        return 0
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        val itemHandler = capability<IItemHandler>()
        if (itemHandler.isPresent) return itemHandler.resolve().get().isItemValid(slot, stack)
        return false
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().receiveEnergy(maxReceive, simulate)
        return 0
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().extractEnergy(maxExtract, simulate)
        return 0
    }

    override fun getEnergyStored(): Int {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().energyStored
        return 0
    }

    override fun getMaxEnergyStored(): Int {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().maxEnergyStored
        return 0
    }

    override fun canExtract(): Boolean {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().canExtract()
        return false
    }

    override fun canReceive(): Boolean {
        val capability = capability<IEnergyStorage>()
        if (capability.isPresent) return capability.resolve().get().canReceive()
        return false
    }

    override fun getTanks(): Int {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().tanks
        return 0
    }

    override fun getFluidInTank(tank: Int): FluidStack {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().getFluidInTank(tank)
        return FluidStack.EMPTY
    }

    override fun getTankCapacity(tank: Int): Int {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().getTankCapacity(tank)
        return 0
    }

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().isFluidValid(tank, stack)
        return false
    }

    override fun fill(resource: FluidStack?, action: IFluidHandler.FluidAction?): Int {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().fill(resource, action)
        return 0
    }

    override fun drain(resource: FluidStack?, action: IFluidHandler.FluidAction?): FluidStack {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().drain(resource, action)
        return FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction?): FluidStack {
        val capability = capability<IFluidHandler>()
        if (capability.isPresent) return capability.resolve().get().drain(maxDrain, action)
        return FluidStack.EMPTY
    }


}