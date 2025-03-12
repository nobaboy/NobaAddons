package me.nobaboy.nobaaddons.utils.items

//? if >=1.21.5-pre2 {
/*import kotlin.jvm.optionals.getOrNull
*///?}

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList

class NbtCompoundWrapper(private val nbt: NbtCompound) {
	val entries: Map<String, NbtElement> get() {
		//? if >=1.21.5-pre2 {
		/*return nbt.entrySet().associate { it.key to it.value }
		*///?} else {
		return nbt.keys.associateWith { nbt.get(it)!! }
		//?}
	}

	fun getCompound(key: String): NbtCompoundWrapper? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getCompound(key).getOrNull()?.let(::NbtCompoundWrapper)
		*///?} else {
		return (nbt.get(key) as? NbtCompound)?.let(::NbtCompoundWrapper)
		//?}
	}

	fun getList(key: String): NbtList? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getList(key).getOrNull()
		*///?} else {
		return nbt.get(key) as? NbtList
		//?}
	}

	fun getInt(key: String): Int? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getInt(key).getOrNull()
		*///?} else {
		return if(nbt.contains(key)) nbt.getInt(key) else null
		//?}
	}

	fun getString(key: String): String? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getString(key).getOrNull()
		*///?} else {
		return nbt.get(key)?.asString()
		//?}
	}

	fun getLong(key: String): Long? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getLong(key).getOrNull()
		*///?} else {
		return if(nbt.contains(key)) nbt.getLong(key) else null
		//?}
	}

	fun getBoolean(key: String): Boolean? {
		//? if >=1.21.5-pre2 {
		/*return nbt.getBoolean(key).getOrNull()
		*///?} else {
		return if(nbt.contains(key)) nbt.getBoolean(key) else null
		//?}
	}
}