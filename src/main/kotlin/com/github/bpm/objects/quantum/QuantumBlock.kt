package com.github.bpm.objects.quantum

import com.github.bpm.Bpm
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult

/**
 * Our quantum block instance
 */
class QuantumBlock : Block(Properties.of(Material.HEAVY_METAL).lightLevel { 2 }.strength(8.0f).destroyTime(20.5f)),
    EntityBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? =
        Bpm.Tiles.QUANTUM.create(pos, state)

    @Suppress("UNCHECKED_CAST")
    override fun <T : BlockEntity?> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = QuantumTicker as BlockEntityTicker<T>


    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        result: BlockHitResult
    ): InteractionResult {
        if (!world.isClientSide) return InteractionResult.PASS // on client side, don't do anything
        val tile = world.getBlockEntity(pos)
        if (tile is QuantumTile)
            Minecraft.getInstance().setScreen(QuantumScreen(tile))
        return InteractionResult.CONSUME
    }
}