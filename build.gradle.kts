    plugins {
    kotlin("jvm") version "2.2.0"
        id("org.jetbrains.compose") version "1.7.1"
        id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"

    }

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation(compose.material3)
}
    compose.desktop {
        application {
            mainClass = "MainKt"
        }
    }

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

