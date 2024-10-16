package me.nobaboy.nobaaddons.api.party

data class PartySnapshot(
	override var inParty: Boolean,
	override var partyLeader: String?,
	override var partyMembers: MutableList<String>,
	override val isLeader: Boolean
) : IParty
