@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.github.bpmapi.util

import com.github.bpmapi.api.type.Type
import com.mojang.math.Vector3f
import com.mojang.math.Vector4f
import net.minecraft.core.BlockPos
import net.minecraft.nbt.*
import net.minecraftforge.common.util.*
import java.util.*
import kotlin.collections.ArrayList

interface Serial : INBTSerializable<CompoundTag> {
    fun CompoundTag.serialize()
    fun CompoundTag.deserialize()

    override fun serializeNBT(): CompoundTag {
        val tag = CompoundTag()
        tag.serialize()
        return tag
    }

    override fun deserializeNBT(nbt: CompoundTag) = nbt.deserialize()
}

fun CompoundTag.putPrimitive(name: String, value: Any?) = when (value) {
    is Boolean -> {
        putEnum("${name}_type", Type.BOOLEAN)
        putBoolean("${name}_value", value)
    }
    is Int -> {
        putEnum("${name}_type", Type.INT)
        putInt("${name}_value", value)
    }
    is Float -> {
        putEnum("${name}_type", Type.FLOAT)
        putFloat("${name}_value", value)
    }
    is String -> {
        putEnum("${name}_type", Type.STRING)
        putString("${name}_value", value)
    }
    else -> putEnum("${name}_type", Type.NULL)//Unsupported
}

fun CompoundTag.getPrimitive(name: String): Any? {
    return when (getEnum<Type>("${name}_type")) {
        Type.BOOLEAN -> getBoolean("${name}_value")
        Type.INT -> getInt("${name}_value")
        Type.FLOAT -> getFloat("${name}_value")
        Type.STRING -> getString("${name}_value")
        else -> null //Null
    }
}


/**This will write the given list into the compound. **/
inline fun <reified T : INBTSerializable<CompoundTag>> CompoundTag.putList(name: String, list: List<T>): CompoundTag {
    val tag = CompoundTag()
    this.putInt("${name}_list_size", list.size)
    list.forEachIndexed { i, value ->
        tag.put("v_$i", value.serializeNBT())
    }
    this.put("${name}_list", tag)
    return this
}

/**This will read a list of the given type**/
inline fun <reified T : INBTSerializable<CompoundTag>> CompoundTag.getList(name: String): List<T> {
    val tag = this.getCompound("${name}_list") ?: return emptyList()
    val size = this.getInt("${name}_list_size")
    val list = ArrayList<T>()
    for (i in 0 until size) {
        val value = tag.getCompound("v_$i") ?: continue
        if (value !is T) continue
        value.deserializeNBT(value)
        list.add(value)
    }
    return list
}

/**This will write the given list into the compound. **/
inline fun <reified T : INBTSerializable<CompoundTag>> CompoundTag.putDeepList(
    name: String,
    list: List<T>
): CompoundTag {
    val tag = CompoundTag()
    this.putInt("${name}_list_size", list.size)
    list.forEachIndexed { i, value ->
        tag.putClass("c_$i", value::class.java)
        if (value is Serial) {
            with(value) {
                val newTag = CompoundTag()
                newTag.serialize()
                tag.put("v_$i", newTag)
            }
        } else tag.put("v_$i", value.serializeNBT())
    }
    this.put("${name}_list", tag)
    return this
}

/**This will read a list of the given type**/
inline fun <reified T : INBTSerializable<CompoundTag>> CompoundTag.getDeepList(name: String): List<T> {
    val tag = this.getCompound("${name}_list") ?: return emptyList()
    val size = this.getInt("${name}_list_size")
    val list = ArrayList<T>()
    for (i in 0 until size) {
        val value = tag.getCompound("v_$i") ?: continue
        val clazz = tag.getClass("c_$i")
        if (T::class.java.isAssignableFrom(clazz))
            list.add((clazz.newInstance() as T).apply { deserializeNBT(value) })
    }
    return list
}

/**
 * This will write a uuid
 */
fun CompoundTag.putClass(name: String, clazz: Class<*>) {
    this.putString(name, clazz.name)
}

/**
 * This will get a class with the given super type of T
 */
fun CompoundTag.getClass(name: String): Class<*> {
    val clsName = this.getString(name)
    return Class.forName(clsName)
}

/**
 * This will write a uuid
 */
@Deprecated(
    message = "This method already exists in the compound class, I was unaware and am too lazy to remove calls to this at this time. :)"
)
fun CompoundTag.putUUID(name: String, uuid: UUID): CompoundTag {
    this.putLongArray(name, longArrayOf(uuid.mostSignificantBits, uuid.leastSignificantBits))
    return this
}

/**
 * This will read a uuid
 */
/**
 * This will write a uuid
 */
@Deprecated(
    message = "This method already exists in the compound class, I was unaware and am too lazy to remove calls to this at this time. :)"
)
fun CompoundTag.getUUID(name: String): UUID? {
    val array = this.getLongArray(name)
    if (array.size != 2) return null
    return UUID(array[0], array[1])
}

/**
 * This will write a uuid
 */
fun CompoundTag.putBlockPos(name: String, pos: BlockPos): CompoundTag {
    this.putIntArray(name, intArrayOf(pos.x, pos.y, pos.z))
    return this
}

/**
 * This will read a uuid
 */
fun CompoundTag.getBlockPos(name: String): BlockPos {
    val array = this.getIntArray(name)
    if (array.size != 3) return BlockPos.ZERO
    return BlockPos(array[0], array[1], array[2])
}

/**
 * This will put a float array iin the compound tag
 */
fun CompoundTag.putFloatArray(name: String, floatArray: FloatArray) {
    val tag = CompoundTag()
    tag.putInt("size", floatArray.size)
    for (i in floatArray.indices)
        tag.putFloat("f_$i", floatArray[i])
    this.put(name, tag)
}

/**
 * This will read the float array
 */
fun CompoundTag.getFloatArray(name: String): FloatArray {
    val tag = this.getCompound(name)
    val size = tag.getInt("size")
    val array = FloatArray(size)
    for (i in array.indices)
        array[i] = tag.getFloat("f_$i")
    return array
}

/**
 * This will write a uuid
 */
inline fun <reified T : Enum<*>> CompoundTag.putEnum(name: String, enum: T): CompoundTag {
    this.putInt(name, enum.ordinal)
    return this
}

/**
 * This will read a uuid
 */
inline fun <reified T : Enum<*>> CompoundTag.getEnum(name: String): T {
    return T::class.java.enumConstants[this.getInt(name)]
}

///**Puts a vector2 value**/
//fun CompoundTag.putVec2(name: String, vec: Vector2f): CompoundTag {
//    this.putFloatArray("${name}_vec2", floatArrayOf(vec.x, vec.y))
//    return this
//}

/**Puts a vector3 value**/
fun CompoundTag.putVec3(name: String, vec: Vector3f): CompoundTag {
    this.putFloatArray("${name}_vec3", floatArrayOf(vec.x(), vec.y(), vec.z()))
    return this
}

/**Puts a vector3 value**/
fun CompoundTag.putVec4(name: String, vec: Vector4f): CompoundTag {
    this.putFloatArray("${name}_vec4", floatArrayOf(vec.x(), vec.y(), vec.z(), vec.w()))
    return this
}

///**Gets a vector2f with the given name**/
//fun CompoundTag.getVec2(name: String): Vector2f {
//    val array = this.getFloatArray("${name}_vec2")
//    assert(array.size == 2) { "Attempted to read vec2 from float array, but found invalid size of ${array.size}" }
//    return Vector2f(array[0], array[1])
//}

/**Gets a vector2f with the given name**/
fun CompoundTag.getVec3(name: String): Vector3f {
    val array = this.getFloatArray("${name}_vec3")
    assert(array.size == 3) { "Attempted to read vec3 from float array, but found invalid size of ${array.size}" }
    return Vector3f(array[0], array[1], array[2])
}

/**Gets a vector2f with the given name**/
fun CompoundTag.getVec4(name: String): Vector4f {
    val array = this.getFloatArray("${name}_vec4")
    assert(array.size == 4) { "Attempted to read vec3 from float array, but found invalid size of ${array.size}" }
    return Vector4f(array[0], array[1], array[2], array[3])
}
//
///**Writes a packet buffer's class by simply putting the name**/
//fun PacketBuffer.writeClass(clazz: Class<*>): PacketBuffer = this.writeString(clazz.name)
//
///**This will attempt to read the class with the reified type and cast it to the given type**/
//fun PacketBuffer.readClass(): Class<*>? {
//    val name = this.readString()
//    return try {
//        Class.forName(name)
//    } catch (notFound: ClassNotFoundException) {
//        println("Failed to find class by name $name")
//        null
//    }
//}