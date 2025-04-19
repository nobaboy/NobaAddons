pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.kikugie.dev/releases")
		maven("https://repo.nea.moe/releases")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.5+"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		versions("1.21", "1.21.3", "1.21.4", "1.21.5")
		vcsVersion = "1.21.4"
	}
	create(rootProject)
}

rootProject.name = "nobaaddons"