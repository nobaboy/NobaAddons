package me.nobaboy.nobaaddons.repo

import com.google.gson.JsonObject
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

interface IRepoObject {
	fun load()

	/**
	 * Loads the correct class type by writing the given [JsonObject] to a stream to re-load through GSON
	 */
	fun <T> adapt(json: JsonObject, cls: Class<T>): T {
		val stream = ByteArrayInputStream(json.toString().toByteArray()) // .close() is a noop, so no need to wrap in use { }
		return InputStreamReader(stream).use { Repo.GSON.fromJson(it, cls) }
	}
}