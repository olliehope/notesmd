import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("dev.kikugie.loom-back-compat")
    id("org.jetbrains.kotlin.jvm")
}

group = sc.properties.get<String>("mod.group")
version = sc.properties.get<String>("mod.version")
val modId: String = sc.properties["mod.id"]
val minecraftVersion = sc.current.version
val requiredJava = sc.properties.get<Int>("java.version")
base.archivesName = "$modId-${project.version}+mc$minecraftVersion-fabric"

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    // Official Mojang mappings + remapping before 26.1; no mappings in the unobfuscated era.
    loomx.applyMojangMappings()
    // The compatibility plugin converts these configurations to implementation on 26.1+.
    modImplementation("net.fabricmc:fabric-loader:${sc.properties.get<String>("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${sc.properties.get<String>("deps.fabric_api")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${sc.properties.get<String>("deps.fabric_language_kotlin")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    // The entire mod is client-only, so a second source set brings no benefit here.
    runConfigs.remove(runConfigs.getByName("server"))
    runConfigs.named("client") {
        generateRunConfig = true
        preferGradleTask = true
        runDirectory = rootProject.file("run/$minecraftVersion")
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(requiredJava)
}

kotlin {
    jvmToolchain(requiredJava)
    compilerOptions.jvmTarget = JvmTarget.fromTarget(requiredJava.toString())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = requiredJava
}

val metadata = mapOf<String, String>(
    "mod_id" to modId,
    "mod_name" to sc.properties["mod.name"],
    "mod_version" to project.version.toString(),
    "mod_author" to sc.properties["mod.author"],
    "mod_description" to sc.properties["mod.description"],
    "mod_homepage" to sc.properties["mod.homepage"],
    "mod_sources" to sc.properties["mod.sources"],
    "mod_issues" to sc.properties["mod.issues"],
    "mod_license" to sc.properties["mod.license"],
    "mod_icon" to sc.properties["mod.icon"],
    // Exact compatibility until wider compatibility has actually been tested.
    "minecraft_version" to minecraftVersion,
    "java_version" to requiredJava.toString(),
    "loader_version" to sc.properties["deps.fabric_loader"],
    "fabric_api_version" to sc.properties["deps.fabric_api"],
    "fabric_language_kotlin_version" to sc.properties["deps.fabric_language_kotlin"],
)

tasks.processResources {
    inputs.properties(metadata)
    filteringCharset = "UTF-8"
    exclude("**/.gitkeep")
    filesMatching("fabric.mod.json") {
        val escaped = metadata.mapValues { (_, value) -> JsonOutput.toJson(value).removeSurrounding("\"") }
        expand(escaped) { escapeBackslash = true }
    }
}

tasks.withType<Jar>().configureEach {
    // The version is already embedded in archivesName to get the requested exact filename.
    archiveVersion = ""
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    from(rootProject.file("LICENSE")) { rename { "LICENSE-$modId" } }
}

// Remapping Loom assigns archiveVersion separately; pin the production filename explicitly.
loomx.modJar.configure {
    archiveFileName = "$modId-${project.version}+mc$minecraftVersion-fabric.jar"
}

val validateMetadata = tasks.register("validateMetadata") {
    group = "verification"
    description = "Validates expanded client-only Fabric metadata and the packaged icon path."
    dependsOn(tasks.processResources)
    val resources = tasks.processResources.map { it.destinationDir }
    inputs.dir(resources)
    doLast {
        val resourceDir = resources.get()
        val text = resourceDir.resolve("fabric.mod.json").readText()
        check(!text.contains("\${")) { "Fabric metadata contains unexpanded placeholders" }
        val json = JsonSlurper().parseText(text) as Map<*, *>
        check(json["schemaVersion"] == 1 && json["id"] == modId)
        check(json["environment"] == "client") { "This mod must stay client-only" }
        check(resourceDir.resolve(json["icon"] as String).isFile) { "Mod icon is missing" }
        val depends = json["depends"] as Map<*, *>
        check(depends["minecraft"] == minecraftVersion)
        check(depends.keys.containsAll(listOf("java", "fabricloader", "fabric-api", "fabric-language-kotlin")))
    }
}

tasks.check { dependsOn(validateMetadata) }

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds this target and collects its production jar in the root build/libs directory."
    dependsOn(tasks.build)
    from(loomx.modJar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
}
