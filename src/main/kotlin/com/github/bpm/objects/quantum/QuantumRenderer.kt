package com.github.bpm.objects.quantum

import com.mojang.blaze3d.vertex.PoseStack
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
import net.minecraftforge.client.model.data.EmptyModelData

class QuantumRenderer(private val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<QuantumTile> {
    private var rot = 0.0
    private fun renderBlock(
        tile: QuantumTile,
        stack: PoseStack,
        source: MultiBufferSource,
        combinedOverlay: Int,
        packedLight: Int
    ) {
        val bakedmodel: BakedModel = context.blockRenderDispatcher.getBlockModel(Blocks.COBBLESTONE.defaultBlockState())
        val i = Minecraft.getInstance().blockColors.getColor(Blocks.COBBLESTONE.defaultBlockState(), null, null, 0)
        val f = (i shr 16 and 255).toFloat() / 255.0f
        val f1 = (i shr 8 and 255).toFloat() / 255.0f
        val f2 = (i and 255).toFloat() / 255.0f
//        context.blockRenderDispatcher.modelRenderer.renderModel(
//            stack.last(),
//            source.getBuffer(ItemBlockRenderTypes.getRenderType(Blocks.COBBLESTONE.defaultBlockState(), false)),
////            source.getBuffer(net.minecraft.client.renderer.RenderType.cutoutMipped()),
//            Blocks.COBBLESTONE.defaultBlockState(),
//            bakedmodel,
//            f,
//            f1,
//            f2,
//            combinedOverlay,
//            packedLight,
//            EmptyModelData.INSTANCE
//        )
    }

    override fun render(
        tile: QuantumTile,
        partialTicks: Float,
        stack: PoseStack,
        source: MultiBufferSource,
        combinedOverlay: Int,
        packedLight: Int
    ) {
        //Minecraft.getInstance().blockRenderer.renderSingleBlock(Blocks.GLASS.defaultBlockState(), stack, source, combinedOverlay, packedLight, EmptyModelData.INSTANCE)
//        context.blockRenderDispatcher.renderSingleBlock(Blocks.GLASS.defaultBlockState(), stack, source, combinedOverlay, packedLight, EmptyModelData.INSTANCE)
//        renderBlock(tile, stack, source, combinedOverlay, packedLight)
        rot += Minecraft.getInstance().deltaFrameTime
        rot %= 360
        stack.pushPose()
        stack.translate(0.5, 1.0, 0.5)
        stack.mulPose(Quaternion.fromXYZ(0f, Math.toRadians(rot).toFloat(), 0f))
        Minecraft.getInstance().itemRenderer.renderStatic(
            ItemStack(Items.DIAMOND),
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