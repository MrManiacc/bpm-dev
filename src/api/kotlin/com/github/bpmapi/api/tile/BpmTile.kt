package com.github.bpmapi.api.tile

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class BpmTile(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state) {
    override fun saveAdditional(tag: CompoundTag) = saveTag(tag)

    override fun setLevel(p_155231_: Level) {
        super.setLevel(p_155231_)

    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        loadTag(tag)
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        load(tag)
    }

    override fun onDataPacket(net: Connection, pkt: ClientboundBlockEntityDataPacket) {
        super.onDataPacket(net, pkt)
        handleUpdateTag(pkt.tag!!)
    }

    fun update() {
        requestModelDataUpdate()
        setChanged()
        level?.setBlockAndUpdate(this.worldPosition, blockState)
    }


    protected abstract fun saveTag(tag: CompoundTag)
    protected abstract fun loadTag(tag: CompoundTag)

    override fun getUpdateTag(): CompoundTag = serializeNBT()
    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
}