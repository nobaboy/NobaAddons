import moe.nea.mcautotranslations.gradle.CollectTranslations
import nobaaddonsbuild.DownloadBackupRepo
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
	id("fabric-loom")
	kotlin("jvm") version("2.1.0")
	kotlin("plugin.serialization") version "2.1.0"
	id("me.modmuss50.mod-publish-plugin")
	id("moe.nea.mc-auto-translations") version "0.1.0"
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
val isSnapshot = runCatching { property("mc.snapshot") }.getOrNull() != null

val isCi = System.getenv("CI") != null

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
	fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
		forRepository { maven(url) }
		filter { groups.forEach(::includeGroup) }
	}

	mavenCentral()
	strictMaven("https://maven.isxander.dev/releases", "dev.isxander", "org.quiltmc.parsers") // YACL
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") // ModMenu
	strictMaven("https://maven.celestialfault.dev/releases", "dev.celestialfault") // CelestialConfig, Commander
	strictMaven("https://repo.hypixel.net/repository/Hypixel/", "net.hypixel") // Hypixel Mod API
	strictMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner") // DevAuth
	strictMaven("https://repo.nea.moe/releases", "moe.nea.mcautotranslations") // mc-auto-translations (which doesn't document anywhere that you need this!!!!)
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
}

dependencies {
	fun devEnvOnly(dependencyNotation: String, allowInSnapshot: Boolean = false) {
		if(!isCi && (allowInSnapshot || !isSnapshot)) modRuntimeOnly(dependencyNotation)
	}

	fun includeImplementation(dependencyNotation: String, mod: Boolean = false, configuration: Action<ExternalModuleDependency> = Action { }) {
		if(mod) modImplementation(dependencyNotation, configuration) else implementation(dependencyNotation, configuration)
		include(dependencyNotation, configuration)
	}

	minecraft("com.mojang:minecraft:${mcVersion}")
	mappings("net.fabricmc:yarn:${mcVersion}+build.${deps["yarn_build"]}:v2")
	modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")

	// strip out -pre and -rc versions
	modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}+${mcVersion.takeWhile { it != '-' }}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${deps["kotlin"]}")

	modImplementation("dev.isxander:yet-another-config-lib:${deps["yacl"]}-fabric") // YACL
	modImplementation("com.terraformersmc:modmenu:${deps["modmenu"]}") // ModMenu

	includeImplementation("dev.celestialfault:commander:${deps["commander"]}", mod = true) { isTransitive = false }
	includeImplementation("dev.celestialfault:celestial-config:${deps["celestialconfig"]}")
	includeImplementation("com.moulberry:mixinconstraints:${deps["mixinconstraints"]}") { isTransitive = false }

	implementation("net.hypixel:mod-api:${deps["hypixel_mod_api"]}")
	devEnvOnly("maven.modrinth:hypixel-mod-api:${deps["hypixel_mod_api_mod"]}")

	devEnvOnly("me.djtheredstoner:DevAuth-fabric:${deps["devauth"]}", allowInSnapshot = true) // DevAuth

	devEnvOnly("maven.modrinth:sodium:${deps["sodium"]}-fabric") // Sodium
	devEnvOnly("maven.modrinth:no-telemetry:${deps["no_telemetry"]}", allowInSnapshot = true) // No Telemetry
	devEnvOnly("maven.modrinth:compacting:${deps["compacting"]}", allowInSnapshot = true) // Compacting

	if(DefaultNativePlatform.getCurrentOperatingSystem().isLinux) {
		devEnvOnly("maven.modrinth:fix-keyboard-on-linux:${deps["fix-linux-keyboard"]}")
	}
}

loom {
	runConfigs {
		removeIf { it.environment == "server" }
	}

	runConfigs.all {
		ideConfigGenerated(stonecutter.current.isActive)
		runDir = "../../run"
		vmArgs("-Dnobaaddons.repoDir=${rootProject.layout.projectDirectory.dir("repo")}")
	}
}

val targetJava = 21

java {
	targetCompatibility = JavaVersion.toVersion(targetJava)
	sourceCompatibility = JavaVersion.toVersion(targetJava)
}

kotlin {
	jvmToolchain(targetJava)
}

mcAutoTranslations {
	translationFunction.set("me.nobaboy.nobaaddons.utils.tr")
	translationFunctionResolved.set("me.nobaboy.nobaaddons.utils.trResolved")
}

val collectTranslations by tasks.registering(CollectTranslations::class) {
	this.classes.from(sourceSets.main.get().kotlin.classesDirectory)
}

// require that this is registered on the root project to avoid running this multiple times per build
val includeBackupRepo = runCatching { rootProject.tasks.withType<DownloadBackupRepo>().named("includeBackupRepo").get() }.getOrNull()
	?: rootProject.tasks.create("includeBackupRepo", DownloadBackupRepo::class) {
		this.outputDirectory = rootProject.layout.buildDirectory.dir("downloadedRepo")
		this.branch = "main"
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
	from(includeBackupRepo)
	from(collectTranslations) {
		into("assets/${mod.id}/lang")
	}
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
		// NOTE: this requires that .github/extract_changelog.py is run first
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
