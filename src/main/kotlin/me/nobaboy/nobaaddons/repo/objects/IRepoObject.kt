package me.nobaboy.nobaaddons.repo.objects

/**
 * Basic repository object type. Implement this on any custom objects that can't be easily
 * represented by any of the default types.
 *
 * @see RepoConstants
 * @see RepoObject
 * @see RepoObjectArray
 * @see RepoObjectMap
 */
interface IRepoObject {
	fun load()
}