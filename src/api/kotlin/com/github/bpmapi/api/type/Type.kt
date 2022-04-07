package com.github.bpmapi.api.type

import com.github.bpmapi.util.Serial
import com.github.bpmapi.util.putEnum
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.client.model.b3d.B3DModel.Face
import javax.lang.model.type.NullType

enum class Type(val typeName: String, val typeClass: Class<*>) {
    BOOLEAN("Boolean", Boolean::class.javaObjectType),
    INT("Int", Int::class.javaObjectType),
    FLOAT("Float", Float::class.javaObjectType),
    STRING("String", Double::class.javaObjectType),
    NULL("Null", Unit::class.java),
    BLOCK_POS("BlockPos", BlockPos::class.java),
    BLOCK_FACE("BlockFace", Direction::class.java),
}