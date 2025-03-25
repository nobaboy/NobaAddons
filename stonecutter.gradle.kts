plugins {
	id("dev.kikugie.stonecutter")
	id("fabric-loom") version "1.10-SNAPSHOT" apply false
	id("me.modmuss50.mod-publish-plugin") version "0.7.+" apply false
}
stonecutter active "1.21.4" /* [SC] DO NOT EDIT */

// Builds every version into `build/libs/{mod.version}/`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = "project"
	ofTask("buildAndCollect")
}

// Publishes every version
stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
	group = "project"
	ofTask("publishMods")
}
