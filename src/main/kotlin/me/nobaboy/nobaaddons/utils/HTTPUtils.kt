package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object HTTPUtils {
	val client: HttpClient = HttpClient.newHttpClient()

	inline fun <reified T> fetchJson(url: String, crossinline requestBuilder: (HttpRequest.Builder) -> Unit = {}): CompletableFuture<T> {
		return CompletableFuture.supplyAsync {
			val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().also(requestBuilder).build()
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())
			check(response.statusCode() < 400) { "Failed to fetch JSON: ${response.statusCode()}" }
			NobaAddons.GSON.fromJson(response.body(), T::class.java)
		}
	}
}