plugins {
	`kotlin-dsl`
	kotlin("plugin.serialization") version "2.0.20"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
	// currently unused, might be used in the future
//	implementation("io.github.classgraph:classgraph:4.8.179")
}
