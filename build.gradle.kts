plugins {
	id("fabric-loom")
	kotlin("jvm") version("2.0.21")
	id("me.modmuss50.mod-publish-plugin")
}

class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
}

class ModDependencies {
	operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()

val isCi = System.getenv("CI") != null

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
	maven("https://maven.isxander.dev/releases") // YACL
	maven("https://maven.terraformersmc.com/") // ModMenu
	maven("https://maven.celestialfault.dev/snapshots") // CelestialConfig
	maven("https://repo.hypixel.net/repository/Hypixel/") // Hypixel Mod API
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	fun devEnvOnly(dependencyNotation: String) {
		if(isCi) return
		modRuntimeOnly(dependencyNotation)
	}

	minecraft("com.mojang:minecraft:${mcVersion}")
	mappings("net.fabricmc:yarn:${mcVersion}+build.${deps["yarn_build"]}:v2")
	modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}+${mcVersion}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${deps["kotlin"]}")

	modImplementation("dev.isxander:yet-another-config-lib:${deps["yacl"]}-fabric") // YACL
	modImplementation("com.terraformersmc:modmenu:${deps["modmenu"]}") // ModMenu

	// CelestialConfig
	implementation("dev.celestialfault:celestial-config:${deps["celestialconfig"]}")
	include("dev.celestialfault:celestial-config:${deps["celestialconfig"]}")

	// Hypixel Mod API
	implementation("net.hypixel:mod-api:${deps["hypixel_mod_api"]}")
	modRuntimeOnly("maven.modrinth:hypixel-mod-api:${deps["hypixel_mod_api_mod"]}")

	modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${deps["devauth"]}") // DevAuth

	devEnvOnly("maven.modrinth:sodium:${deps["sodium"]}") // Sodium
	devEnvOnly("maven.modrinth:no-telemetry:${deps["no_telemetry"]}") // No Telemetry
}

loom {
	runConfigs {
		removeIf { it.environment == "server" }
	}

	runConfigs.all {
		ideConfigGenerated(stonecutter.current.isActive)
		runDir = "../../run"
	}
}

val targetJava = if(stonecutter.compare(mcVersion, "1.20.6") >= 0) 21 else 17

java {
	targetCompatibility = JavaVersion.toVersion(targetJava)
	sourceCompatibility = JavaVersion.toVersion(targetJava)
}

kotlin {
	jvmToolchain(targetJava)
}

tasks.processResources {
	inputs.property("id", mod.id)
	inputs.property("name", mod.name)
	inputs.property("version", mod.version)
	inputs.property("mcdep", mcDep)

	val map = mapOf(
		"id" to mod.id,
		"name" to mod.name,
		"version" to mod.version,
		"mcdep" to mcDep
	)

	filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}

publishMods {
	file = tasks.remapJar.get().archiveFile
	displayName = "${mod.version} for ${property("mod.mc_title")}"
	version = "${mod.version}+$mcVersion"
	changelog = runCatching {
		// NOTE: this requires running .github/extract_changelog.py first
		rootProject.file("CHANGELOG.mini").readText()
	}.getOrDefault("See the full changelog at https://github.com/nobaboy/NobaAddons/blob/master/CHANGELOG.md")
	type = ALPHA
	modLoaders.add("fabric")

	dryRun = !providers.environmentVariable("MODRINTH_TOKEN").isPresent

	modrinth {
		projectId = property("publish.modrinth").toString()
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		minecraftVersions.addAll(property("mod.mc_targets").toString().split(" "))
		requires("fabric-api")
		requires("fabric-language-kotlin")
		requires("yacl")
		requires("hypixel-mod-api")
		optional("modmenu")
	}
}