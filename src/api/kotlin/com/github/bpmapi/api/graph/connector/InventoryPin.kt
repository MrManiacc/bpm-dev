package com.github.bpmapi.api.graph.connector

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.ItemStackHandler


open class InventoryPin(
    name: String = "itemstack", maxStacks: Int
) : Pin(name), CapabilityPin {
    private val items = ItemStackHandler(maxStacks)

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
    }

    override fun CompoundTag.deserialize() {
        items.deserializeNBT(getCompound("items"))
    }

    override fun getSlots(): Int = items.slots

    override fun getStackInSlot(slot: Int): ItemStack = items.getStackInSlot(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        items.insertItem(slot, stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        items.extractItem(slot, amount, simulate)

    override fun getSlotLimit(slot: Int): Int = items.getSlotLimit(slot)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = items.isItemValid(slot, stack)


}