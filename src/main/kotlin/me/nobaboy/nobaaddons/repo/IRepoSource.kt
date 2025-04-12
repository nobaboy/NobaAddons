package me.nobaboy.nobaaddons.repo

/**
 * Basic repository object type. Implement this on any custom objects that can't be easily
 * represented by any of the default types.
 *
 * @see RepoSource.Single
 * @see RepoSource.Map
 * @see RepoSource.List
 */
interface IRepoSource {
	fun load()
}