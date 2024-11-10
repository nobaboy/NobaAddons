package me.nobaboy.nobaaddons.api.data.party

interface IParty {
	var inParty: Boolean
	var partyLeader: String?
	var partyMembers: MutableList<String>
	val isLeader: Boolean
}