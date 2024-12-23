package me.nobaboy.nobaaddons.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

object HTTPUtils {
	val client = HttpClient()

	init {
		ClientLifecycleEvents.CLIENT_STOPPING.register { client.close() }
	}

	inline fun <reified T> fetchJson(
		url: String, serializer: KSerializer<T> = serializer<T>(), crossinline builder: HttpRequestBuilder.() -> Unit = {}
	): Deferred<T> = NobaAddons.coroutineScope.async {
		val response = client.get(url, builder)
		NobaAddons.JSON.decodeFromString(serializer, response.body())
	}
}