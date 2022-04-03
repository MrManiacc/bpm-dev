package com.github.bpmapi.api.type

import com.github.bpmapi.util.Serial
import com.github.bpmapi.util.putEnum
import net.minecraft.nbt.CompoundTag
import javax.lang.model.type.NullType

enum class Type(val typeName: String, val typeClass: Class<*>) {
    BOOLEAN("Boolean", Boolean::class.javaObjectType),
    INT("Int", Int::class.javaObjectType),
    FLOAT("Float", Float::class.javaObjectType),
    STRING("String", Double::class.javaObjectType),
    NULL("Null", Unit.javaClass);

}