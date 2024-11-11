package me.nobaboy.nobaaddons.utils

import com.google.gson.Gson
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object APIUtils {
	const val HYPIXEL_API_ROOT = "https://api.hypixel.net/v2"

	inline fun <reified T> fetchJson(url: String): CompletableFuture<T> {
		return CompletableFuture.supplyAsync {
			val client = HttpClient.newHttpClient()
			val request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build()

			val response = client.send(request, HttpResponse.BodyHandlers.ofString())

			if (response.statusCode() == 200) {
				Gson().fromJson(response.body(), T::class.java)
			} else {
				throw RuntimeException("Failed to fetch JSON: ${response.statusCode()}")
			}
		}
	}
}