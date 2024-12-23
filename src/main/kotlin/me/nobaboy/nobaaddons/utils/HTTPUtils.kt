package me.nobaboy.nobaaddons.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.serialization.KSerializer
import me.nobaboy.nobaaddons.NobaAddons
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object HTTPUtils {
	val javaClient: java.net.http.HttpClient = java.net.http.HttpClient.newHttpClient()
	val client = HttpClient()

	fun <T> fetchJson(url: String, serializer: KSerializer<T>, builder: HttpRequestBuilder.() -> Unit = {}): Deferred<T> = NobaAddons.coroutineScope.async {
		val response = client.get(url, builder)
		NobaAddons.JSON.decodeFromString(serializer, response.body())
	}

	// TODO remove in favor of ktor
	inline fun <reified T> fetchJson(url: String, crossinline requestBuilder: (HttpRequest.Builder) -> Unit = {}): CompletableFuture<T> {
		return CompletableFuture.supplyAsync {
			val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().also(requestBuilder).build()
			val response = javaClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join()
			check(response.statusCode() < 400) { "Failed to fetch JSON: ${response.statusCode()}" }
			NobaAddons.GSON.fromJson(response.body(), T::class.java)
		}
	}
}