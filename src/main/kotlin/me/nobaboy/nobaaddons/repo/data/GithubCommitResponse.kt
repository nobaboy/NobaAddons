package me.nobaboy.nobaaddons.repo.data

import kotlinx.serialization.Serializable

@Serializable
data class GithubCommitResponse(
	val sha: String,
)
