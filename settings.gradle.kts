pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases")
    }
    plugins {
        id("dev.kikugie.stonecutter") version providers.gradleProperty("stonecutter_version").get()
        id("dev.kikugie.loom-back-compat") version providers.gradleProperty("loom_back_compat_version").get()
        id("org.gradle.toolchains.foojay-resolver-convention") version providers.gradleProperty("foojay_version").get()
        id("org.jetbrains.kotlin.jvm") version providers.gradleProperty("kotlin_version").get()
        id("com.diffplug.spotless") version providers.gradleProperty("spotless_version").get()
    }
}

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.kikugie.loom-back-compat")
    id("org.gradle.toolchains.foojay-resolver-convention")
}

stonecutter {
    create(rootProject) {
        versions("1.21.1", "1.21.5", "1.21.8", "1.21.11", "26.1.2")
        vcsVersion = "1.21.11"
    }
}

rootProject.name = "notesmd"
