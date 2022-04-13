package com.github.bpm.util

import com.github.bpmapi.register.Registry
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.util.LogicalSidedProvider
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.util.thread.SidedThreadGroups
import java.util.concurrent.CompletableFuture

/**
 * This will do ors of the given values.
 */
fun Int.orEquals(vararg ints: Int): Int {
    var out = this
    for (element in ints) out = out or element
    return out
}


/**This boolean checks to see if the current program is on the physical client or not**/
internal val physicalClient: Boolean
    get() = FMLEnvironment.dist == Dist.CLIENT

/**This boolean checks to see if the current program is on the physical server or not**/
internal val physicalServer: Boolean
    get() = FMLEnvironment.dist == Dist.DEDICATED_SERVER

/**This boolean checks to see if the current thread group is thew logical client**/
internal val logicalClient: Boolean
    get() {
        if (physicalServer) return false //This is so we don't end up calling [Minecraft] calls from the client
        if (Thread.currentThread().threadGroup == SidedThreadGroups.CLIENT) return true
        try {
            if (RenderSystem.isOnRenderThread()) return true
        } catch (notFound: ClassNotFoundException) {
            return false //We're not on the client if there's a class not found execetion
        }
        return false
    }


/**This boolean checks to see if the current thread group is thew logical client**/
internal val logicalServer: Boolean
    get() = Thread.currentThread().threadGroup == SidedThreadGroups.SERVER

/**This boolean checks to see if the current thread group is thew logical client**/
internal val logicalRender: Boolean
    get() = RenderSystem.isOnRenderThread()

/**
 * This block of code will execute only if we're on the physical client
 */
internal fun whenClient(logical: Boolean = true, block: () -> Unit) {
    if (logical && logicalClient) block()
    else if (!logical && physicalClient) block()
}

/**
 * This block of code will execute only if we're on the physical client
 */
internal fun whenServer(logical: Boolean = true, block: () -> Unit) {
    if (logical && logicalServer) block()
    else if (!logical && physicalServer) block()
}

/**
 * This will run the given block on the logical side
 */
internal fun runOn(side: LogicalSide, block: () -> Unit): CompletableFuture<Void> {
    val executor = LogicalSidedProvider.WORKQUEUE.get(side)
    return if (!executor.isSameThread)
        executor.submit(block) // Use the internal method so thread check isn't done twice
    else {
        block()
        CompletableFuture.completedFuture(null)
    }
}

/**
 * This run the given chunk of code on the client
 */
internal fun runOnClient(block: () -> Unit): CompletableFuture<Void> {
    return runOn(LogicalSide.CLIENT, block)
}

/**
 * This will run the render method
 */
internal fun runOnRender(block: () -> Unit) {
    if (logicalRender)
        block()
    else
        RenderSystem.recordRenderCall(block)
}

/**
 * This run the given chunk of code on the server
 */
internal fun runOnServer(block: () -> Unit): CompletableFuture<Void> {
    return runOn(LogicalSide.SERVER, block)
}


/**
 * This is used for easy block entity registration
 */
inline fun <reified T : BlockEntity> Registry<BlockEntityType<*>>.tile(
    block: Block,
    crossinline supplier: (Pair<BlockPos, BlockState>) -> T
): BlockEntityType<T> {
    return BlockEntityType.Builder.of({ pos, state -> supplier(pos to state) }, block).build(null)
}


/**
 * This will raytrace the given distance for the given player
 */
fun Player.rayTrace(distance: Double = 75.0): BlockHitResult {
    val rayTraceResult = pick(distance, 0f, false) as BlockHitResult
    var xm = rayTraceResult.location.x
    var ym = rayTraceResult.location.y
    var zm = rayTraceResult.location.z
    var pos = BlockPos(xm, ym, zm)
    val block = level.getBlockState(pos)
    if (block.isAir) {
        if (rayTraceResult.direction == Direction.SOUTH)
            zm--
        if (rayTraceResult.direction == Direction.EAST)
            xm--
        if (rayTraceResult.direction == Direction.UP)
            ym--
    }
    pos = BlockPos(xm, ym, zm)
    return BlockHitResult(rayTraceResult.location, rayTraceResult.direction, pos, false)
}