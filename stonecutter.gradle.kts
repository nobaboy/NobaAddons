plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.7-SNAPSHOT" apply false
}
stonecutter active "1.21" /* [SC] DO NOT EDIT */

// Builds every version into `build/libs/{mod.version}/`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter configureEach {
    /* https://stonecutter.kikugie.dev/ */
    dependency("fapi", project.property("deps.fabric_api").toString())
}
