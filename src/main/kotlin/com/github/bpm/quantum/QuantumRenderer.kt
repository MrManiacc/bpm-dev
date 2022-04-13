package com.github.bpm.quantum

import com.github.bpm.util.RenderTypes
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix4f
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import org.lwjgl.opengl.GL11

class QuantumRenderer(private val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<QuantumTile> {
    private var rot = 0.0
    override fun render(
        tile: QuantumTile,
        partialTicks: Float,
        stack: PoseStack,
        source: MultiBufferSource,
        combinedOverlay: Int,
        packedLight: Int
    ) {
        rot += Minecraft.getInstance().deltaFrameTime * 2
        rot %= 360
        stack.pushPose()
        val value = ((rot / 360))
        val yOffset = 0.125 + if (rot <= 180) value else 1 + -value
        stack.translate(0.5, yOffset, 0.5)
        stack.mulPose(Quaternion.fromXYZ(0f, Math.toRadians(rot).toFloat(), 0f))
        Minecraft.getInstance().itemRenderer.renderStatic(
            ItemStack(Items.NETHER_STAR),
            ItemTransforms.TransformType.GROUND,
            15728880,
            OverlayTexture.NO_OVERLAY,
            stack,
            source,
            0
        )
        stack.popPose()
    }


}