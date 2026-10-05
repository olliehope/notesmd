plugins {
    id("dev.kikugie.stonecutter")
    id("com.diffplug.spotless")
}

stonecutter active "1.21.11"

repositories {
    mavenCentral()
}

// Kotlin preprocessing is provided by Stonecutter. Add only compatibility rules you need.
stonecutter parameters {
    constants["fabric"] = true
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
}

// Only authored files in the shared repository; no generated Stonecutter trees.
spotless {
    kotlin {
        target(rootProject.fileTree("src") { include("**/*.kt") })
        ktlint(providers.gradleProperty("ktlint_version").get())
    }
    kotlinGradle {
        target(rootProject.fileTree(".") { include("*.gradle.kts", "gradle/**/*.gradle.kts") })
        ktlint(providers.gradleProperty("ktlint_version").get())
    }
    format("json") {
        target(rootProject.fileTree(".") { include("src/**/*.json", ".github/**/*.json") })
        leadingTabsToSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
    // Preserve intentional Markdown line breaks; avoid heavyweight Node formatters.
    format("markdown") {
        target(rootProject.fileTree(".") { include("*.md", "docs/**/*.md", ".github/**/*.md") })
        endWithNewline()
    }
    format("configuration") {
        target(
            rootProject.fileTree(".") {
                include(".github/**/*.yml", ".github/**/*.yaml", "*.toml", "*.properties", ".editorconfig", ".gitignore", ".gitattributes")
            },
        )
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds and collects production jars for every Minecraft target."
    dependsOn(stonecutter.tasks.named("buildAndCollect"))
}

tasks.register("buildActive") {
    group = "build"
    description = "Builds and collects the currently active Minecraft target."
    dependsOn(stonecutter.tasks.named("buildAndCollect") { metadata.isActive })
}

tasks.register("checkActive") {
    group = "verification"
    description = "Checks formatting and the currently active Minecraft target."
    dependsOn("spotlessCheck")
    dependsOn(stonecutter.tasks.named("check") { metadata.isActive })
}

tasks.register("runActiveClient") {
    group = "fabric"
    description = "Runs a development client for the currently active Minecraft target."
    dependsOn(stonecutter.tasks.named("runClient") { metadata.isActive })
}

tasks.register("checkAll") {
    group = "verification"
    description = "Runs repository formatting verification and all target checks."
    dependsOn("spotlessCheck")
    dependsOn(stonecutter.tasks.named("check"))
}

tasks.named("check") { dependsOn("checkAll") }
