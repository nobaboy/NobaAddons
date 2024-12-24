package me.nobaboy.nobaaddons.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.future.await
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object HTTPUtils {
	val client: HttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()

	init {
		ClientLifecycleEvents.CLIENT_STOPPING.register { client.close() }
	}

	inline fun <reified T> fetchJson(
		url: String, serializer: KSerializer<T> = serializer<T>(), crossinline builder: HttpRequest.Builder.() -> Unit = {}
	): Deferred<T> = NobaAddons.coroutineScope.async {
		val response = client.get(url, builder)
		NobaAddons.JSON.decodeFromString(serializer, response.body())
	}

	suspend inline fun HttpClient.get(uri: String, crossinline builder: HttpRequest.Builder.() -> Unit = {}): HttpResponse<String> = get(uri, HttpResponse.BodyHandlers.ofString(), builder)

	suspend inline fun <T> HttpClient.get(uri: String, handler: HttpResponse.BodyHandler<T>, crossinline builder: HttpRequest.Builder.() -> Unit = {}): HttpResponse<T> {
		val request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).apply(builder).build()
		val response = client.sendAsync(request, handler).await()
		check(response.statusCode() in (200 until 300)) { "Invalid response code ${response.statusCode()}" }
		return response
	}
}