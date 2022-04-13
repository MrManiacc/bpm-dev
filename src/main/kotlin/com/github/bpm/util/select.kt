@file:Suppress("INACCESSIBLE_TYPE")

package com.github.bpm.util

import com.github.bpm.Bpm
import com.github.bpm.quantum.QuantumScreen
import com.github.bpmapi.api.graph.node.ISelectable
import com.github.bpmapi.api.graph.node.Node
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.mojang.math.Vector4f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource.BufferSource
import net.minecraft.client.renderer.RenderStateShard.*
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraftforge.client.event.RenderLevelLastEvent
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty
import net.minecraftforge.network.NetworkEvent
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * This is used to help with selecting of blocks.
 */
object Selections {
    private val selecting: MutableSet<UUID> = HashSet()
    private val isReady: Boolean get() = context != null
    private var context: SelectionContext<*>? = null

    internal fun tick(event: ClientTickEvent) = context?.update() ?: Unit
    internal fun render(event: RenderLevelLastEvent) = context?.render(event) ?: Unit

    internal fun clickEmpty(event: LeftClickEmpty) {
        if (isReady) {
            finish()
        }
    }

    internal fun clickBlock(event: LeftClickBlock) {
        whenClient {
            if (isReady) {
                event.isCanceled = true
                finish()
            }
        }
        whenServer {
            if (selecting.contains(event.player.uuid)) {
                event.isCanceled = true
            }
        }
    }


    internal fun stopSelection(packet: Selection, context: NetworkEvent.Context): Boolean {
        if (packet.start) selecting.add(packet.uuid)
        else selecting.remove(packet.uuid)
        return true
    }

    fun <T : ISelectable> start(node: T) {
        if (isReady) {
            warn { "Attempted to start selection, while selection is in process!" }
            return
        }
        context = SelectionContext(node).start()
    }

    private fun finish() {
        if (!isReady) {
            warn { "Attempted to finish selection, but there is not selection context present" }
            return
        }
        context!!.finish()
        context = null
    }

}

/**
 * This is used to store the context needed to render/update the selection
 */
internal class SelectionContext<T>(private val node: T) where T : ISelectable {
    private val casted: Node get() = node as Node
    private var selectedPos = BlockPos.ZERO
    private var selectedFace = Direction.NORTH
    private val renderer = SelectionRenderer()

    fun start(): SelectionContext<T> {
        Bpm.Packets.sendToServer(Bpm.Packets.Selection {
            start = true
            uuid = Minecraft.getInstance().player?.uuid!!
        })
        Minecraft.getInstance().setScreen(null)
        return this
    }

    fun render(event: RenderLevelLastEvent) {
        renderer.start(event)
        renderer.draw(selectedPos, selectedFace)
        renderer.finish()
    }


    fun update() {
        val player = Minecraft.getInstance().player ?: return
        val result = player.rayTrace()
        this.selectedPos = result.blockPos
        this.selectedFace = result.direction
    }


    fun finish() {
        node.finishSelection(selectedPos, selectedFace)
        casted.graph.tile.pushGraph()
        Bpm.Packets.sendToServer(Bpm.Packets.Selection {
            start = false
            uuid = Minecraft.getInstance().player?.uuid!!
        })
        Minecraft.getInstance().setScreen(QuantumScreen(casted.graph.tile))
    }
}

internal object RenderTypes {
    private val DEPTH_WRITE = WriteMaskStateShard(true, false)
    private val TRANSLUCENT_TRANSPARENCY = TransparencyStateShard("translucent_transparency",
        {
            RenderSystem.enableBlend()
            RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            )
        }
    ) {
        RenderSystem.disableBlend()
        RenderSystem.defaultBlendFunc()
    }

    private val VIEW_OFFSET_Z_LAYERING = LayeringStateShard("view_offset_z_layering", {
        val posestack = RenderSystem.getModelViewStack()
        posestack.pushPose()
        posestack.scale(0.99975586f, 0.99975586f, 0.99975586f)
        RenderSystem.applyModelViewMatrix()
    }) {
        val posestack = RenderSystem.getModelViewStack()
        posestack.popPose()
        RenderSystem.applyModelViewMatrix()
    }
    private val EQUAL_DEPTH_TEST = DepthTestStateShard("always", GL11.GL_ALWAYS)
    private val RENDERTYPE_OUTLINE_SHADER = ShaderStateShard { GameRenderer.getPositionColorShader() }

    private val STATE = RenderType.CompositeState.builder()
        .setDepthTestState(EQUAL_DEPTH_TEST)
        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
        .setWriteMaskState(DEPTH_WRITE)
        .setDepthTestState(EQUAL_DEPTH_TEST)
        .setShaderState(RENDERTYPE_OUTLINE_SHADER)
        .createCompositeState(false)

    val QUAD_RENDERER: RenderType =
        RenderType.create(
            "${Bpm.id}:quad",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            STATE
        )
}

internal class SelectionRenderer {
    private var stack: PoseStack? = null
    private var partialTicks: Float? = null
    private var projectionMatrix: Matrix4f? = null
    private val buffers: BufferSource get() = Minecraft.getInstance().renderBuffers().bufferSource()
    private val projectedView get() = Minecraft.getInstance().gameRenderer.mainCamera.position
    val isReady: Boolean get() = stack != null && partialTicks != null && projectionMatrix != null


    fun start(event: RenderLevelLastEvent) {
        stack = event.poseStack
        partialTicks = event.partialTick
        projectionMatrix = event.projectionMatrix
    }

    fun draw(
        posIn: BlockPos,
        face: Direction,
        color: Vector4f = Vector4f(47f / 255f, 52f / 255f, 204f / 255f, 1f),
        renderType: RenderType = RenderTypes.QUAD_RENDERER
    ) {
        if (!isReady) return
        val builder = buffers.getBuffer(renderType)
        stack!!.pushPose()
        stack!!.translate(-projectedView.x, -projectedView.y, -projectedView.z)
        val matrix = stack!!.last().pose()
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_CULL_FACE)
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()
        val pos = Vector3f(posIn.x.toFloat(), posIn.y.toFloat(), posIn.z.toFloat())
        val min = Vector3f(pos.x() + 0.0f, pos.y() + 0.0f, pos.z() + 0.0f)
        val max = Vector3f(pos.x() + 1.0f, pos.y() + 1.0f, pos.z() + 1.0f)
        when (face) {
            Direction.NORTH -> {
                builder.vertex(matrix, min.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
            Direction.SOUTH -> {
                builder.vertex(matrix, max.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
            Direction.EAST -> {
                builder.vertex(matrix, max.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
            Direction.WEST -> {
                builder.vertex(matrix, min.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
            Direction.UP -> {
                builder.vertex(matrix, max.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), max.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), max.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
            Direction.DOWN -> {
                builder.vertex(matrix, min.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), min.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, max.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
                builder.vertex(matrix, min.x(), min.y(), max.z()).color(color.x(), color.y(), color.z(), color.w())
                    .endVertex()
            }
        }
        stack!!.popPose()
    }

    fun finish() {
        stack = null
        partialTicks = null
        projectionMatrix = null
    }

}