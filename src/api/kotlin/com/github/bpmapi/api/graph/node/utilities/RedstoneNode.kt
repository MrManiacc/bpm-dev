package com.github.bpmapi.api.graph.node.utilities

import com.github.bpmapi.api.event.TickEvent
import com.github.bpmapi.api.graph.connector.CapabilityPin
import com.github.bpmapi.api.graph.connector.EventPin
import com.github.bpmapi.api.graph.connector.StorePin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.Node
import com.github.bpmapi.api.type.Type
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraftforge.energy.IEnergyStorage

class RedstoneNode : Node("Redstone") {
    val SignalIn by input(VarPin("signal in", Type.BLOCK_POS, true))
    val PowerOut by output(VarPin("redstone out", Type.INT, true))
    val DoAction by input(EventPin("event in", ::onEvent))
    val PassEvent by output(EventPin("event out", TickEvent::class)) //"Infinite" item output buffer

    private fun onEvent(actionEvent: TickEvent) {
        if (SignalIn.isLinked()) {
            SignalIn.value = SignalIn.links<VarPin>().firstOrNull()?.value ?: return
        }
        val level = this.graph.tile.level ?: return
        val pos = SignalIn(BlockPos.ZERO) ?: return
        val sig = level.getSignal(pos, Direction.UP)
        PowerOut.value = sig
        println("Updated redstone to: $sig")
        actionEvent.sender = PassEvent
        PassEvent.callEvent(actionEvent)
    }

    private fun redstoneFor(blockPos: BlockPos, level: Level): Int = level.getDirectSignalTo(blockPos)

    override fun CompoundTag.serialize() {
        put("signal", SignalIn.serializeNBT())
        put("action", DoAction.serializeNBT())
        put("pass", PassEvent.serializeNBT())
        put("power", PowerOut.serializeNBT())
    }

    override fun CompoundTag.deserialize() {
        SignalIn.deserializeNBT(getCompound("signal"))
        DoAction.deserializeNBT(getCompound("action"))
        PassEvent.deserializeNBT(getCompound("pass"))
        PowerOut.deserializeNBT(getCompound("power"))
    }

}