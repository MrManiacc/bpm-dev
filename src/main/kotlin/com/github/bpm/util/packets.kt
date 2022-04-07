package com.github.bpm.util

import com.github.bpmapi.net.Packet
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.*

class GraphUpdate : Packet() {
    //    var graph: Graph = Graph()
    var graph = CompoundTag()
    var blockPos: BlockPos = BlockPos.ZERO
    var world: ResourceKey<Level> = Level.OVERWORLD

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeNbt(graph)

        buffer.writeBlockPos(blockPos)
        buffer.writeResourceLocation(world.location())
    }

    override fun read(buffer: FriendlyByteBuf) {
        graph = buffer.readNbt()!!
        blockPos = buffer.readBlockPos()
        world = ResourceKey.create(Registry.DIMENSION_REGISTRY, buffer.readResourceLocation());
    }

}

class GraphRequest : Packet() {
    var blockPos: BlockPos = BlockPos.ZERO
    var world: ResourceKey<Level> = Level.OVERWORLD

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeBlockPos(blockPos)
        buffer.writeResourceLocation(world.location())
    }

    override fun read(buffer: FriendlyByteBuf) {
        blockPos = buffer.readBlockPos()
        val loc = buffer.readResourceLocation()
        world = ResourceKey.create(Registry.DIMENSION_REGISTRY, loc);
    }

}
