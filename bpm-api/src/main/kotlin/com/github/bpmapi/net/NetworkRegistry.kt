package com.github.bpmapi.net

import com.github.bpmapi.register.IRegister
import com.github.bpmapi.util.debug
import com.github.bpmapi.util.info
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.BiConsumer
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class NetworkRegistry : IRegister {
    private var currentId = 0
    private lateinit var instance: SimpleChannel
    protected val listeners: MutableMap<Class<out Packet>, PacketWrapper<Packet>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun register(modBus: IEventBus, forgeBus: IEventBus, modId: String) {
        val name = "${modId}_network"
        debug { "Registering bmp network instance with name: $name" }
        modBus.addListener { _: FMLCommonSetupEvent ->
            instance = NetworkRegistry.newSimpleChannel(
                ResourceLocation(modId, name),
                { "1.0" },
                { true },
                { true }
            )
            info { "Registering packets for network '$name'..." }
            for (packet in listeners) {
                val packetClass = packet.key
                val listener = packet.value
                instance.messageBuilder(packetClass as Class<Packet>, currentId++)
                    .encoder { pkt, buffer ->
                        pkt.write(buffer)
                    }
                    .decoder {
                        val newPacket = listener()
                        newPacket.read(it)
                        newPacket
                    }
                    .consumer(BiConsumer { pkt, ctx ->
                        listener.invoke(pkt, ctx.get())
                    })
                    .add()
            }
        }

    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T : Packet> register(
        noinline packetSupplier: () -> T
    ): ReadOnlyProperty<Any?, PacketWrapper<T>> {
        val packetListener = PacketWrapper(packetSupplier)
        listeners[T::class.java] = packetListener as PacketWrapper<Packet>
        return object : ReadOnlyProperty<Any?, PacketWrapper<T>>, Supplier<PacketWrapper<T>> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = packetListener
            override fun get() = packetListener
        }
    }


    /**
     * This will broadcast to all clients.
     *
     * @param packet the packet to broadcast
     */
    fun sendToAllClients(
        packet: Packet
    ) = instance.send<Any>(PacketDistributor.ALL.noArg(), packet)

    /**
     * This will broadcast to all the clients with the specified chunk.
     *
     * @param packet the packet to send
     * @param chunk  the chunk to use
     */
    fun sendToClientsWithChunk(packet: Packet, chunk: LevelChunk) =
        instance.send<Any>(PacketDistributor.TRACKING_CHUNK.with { chunk }, packet)


    /**
     * This will broadcast to all the clients with the specified chunk.
     *
     * @param packet the packet to send
     * @param near   The target point to use as reference for what is near
     */
    fun sendToClientsWithBlockLoaded(
        packet: Packet,
        blockPos: BlockPos,
        world: Level
    ) = instance.send<Any>(PacketDistributor.TRACKING_CHUNK.with { world.getChunkAt(blockPos) }, packet)

    /**
     * This will send the packet directly to the server
     *
     * @param packet the packet to be sent
     */
    fun <T : Packet> sendToServer(packet: T) =
        instance.sendToServer(packet)

    /**
     * This will send the packet directly to the given player
     *
     * @param packet the packet to send to the client
     * @param player the player to recieve the packet
     */
    fun sendToClient(packet: Packet, player: ServerPlayer) {
        instance.sendTo<Any>(
            packet,
            player.connection.connection,
            NetworkDirection.PLAY_TO_CLIENT
        )
    }
    /**
     * This will send the packet directly to the given player
     *
     * @param packet the packet to send to the client
     * @param player the player to recieve the packet
     */
    fun NetworkEvent.Context.sendToClient(packet: Packet) {
        instance.sendTo<Any>(
            packet,
            this.networkManager,
            NetworkDirection.PLAY_TO_CLIENT
        )
    }
}