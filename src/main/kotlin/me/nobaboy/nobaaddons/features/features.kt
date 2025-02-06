package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.features.general.DevFeatures
import me.nobaboy.nobaaddons.features.qol.MouseLock
import me.nobaboy.nobaaddons.features.slayers.CompactSlayerMessages
import me.nobaboy.nobaaddons.features.slayers.MiniBossFeatures

internal val FEATURES = arrayOf<Feature>(
	// region General
	// endregion

	// region UI & Visual
	// endregion

	// region Inventory
	// endregion

	// region Events
	// endregion

	// region Slayers
	MiniBossFeatures,
	CompactSlayerMessages,
	// endregion

	// region Fishing
	// endregion

	// region Mining
	// endregion

	// region Dungeons
	// endregion

	// region Rift
	// endregion

	// region Chat
	// endregion

	// region QOL
	MouseLock,
	// endregion

	// region Dev
	DevFeatures,
	// endregion
)