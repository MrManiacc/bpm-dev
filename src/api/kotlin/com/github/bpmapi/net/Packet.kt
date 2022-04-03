package com.github.bpmapi.net

import com.github.bpmapi.util.debug
import com.github.bpmapi.util.warn
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.packs.repository.Pack
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent

abstract class Packet {
    abstract fun write(buffer: FriendlyByteBuf)
    abstract fun read(buffer: FriendlyByteBuf)
}
