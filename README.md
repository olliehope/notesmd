# Notes MD

`notesmd` is the foundation for a **client-only Fabric mod written in Kotlin**. The intended project is a generic Markdown-style in-game notes application with pages, tabs, and links between notes. Those features have not been implemented.

This repository currently contains build infrastructure, metadata, an empty Kotlin source directory, and a placeholder icon. It contains **zero Kotlin or Java implementation source files**. Fabric metadata intentionally has no entrypoints or mixin declarations; the resulting scaffold JAR is inert and provides no gameplay functionality.

## Supported targets

| Minecraft | Game / compilation Java |
| --- | --- |
| 1.21.1 | 21 |
| 1.21.5 | 21 |
| 1.21.8 | 21 |
| 1.21.11 | 21 |
| 26.1.2 | 25 |

All five exact versions are configured. Metadata declares exact Minecraft compatibility until broader compatibility has been tested. The committed active development version is **1.21.11**.

**Install JDK 25 to run Gradle**, including when building a Java 21 target. Gradle Java and Kotlin toolchains select the compilation version from the target configuration. The Foojay resolver can download missing compilation JDKs; it cannot provide the JVM needed to start Gradle. Set `JAVA_HOME` and IntelliJ's Gradle JVM to JDK 25. Initial setup needs internet access to resolve the wrapper, Minecraft, dependencies, and toolchains.

## Build and development commands

From the repository root in PowerShell:

| Action | Command |
| --- | --- |
| Check the wrapper and launcher JVM | `.\gradlew.bat --version` |
| Build and collect the active target | `.\gradlew.bat :buildActive` |
| Build and collect every target | `.\gradlew.bat :buildAll` |
| Build a specific target | `.\gradlew.bat :1.21.8:buildAndCollect` |
| Run the active development client | `.\gradlew.bat :runActiveClient` |
| Run a specific development client | `.\gradlew.bat :26.1.2:runClient` |
| Apply formatting | `.\gradlew.bat :spotlessApply` |
| Verify formatting | `.\gradlew.bat :spotlessCheck` |
| Check the active target and formatting | `.\gradlew.bat :checkActive` |
| Check every target and formatting | `.\gradlew.bat :checkAll` |
| Switch the active target | `.\gradlew.bat "Set active project to 1.21.8"` |
| Restore the committed active target | `.\gradlew.bat "Reset active project"` |
| Reprocess the current shared sources | `.\gradlew.bat "Refresh active project"` |
| Inspect available tasks | `.\gradlew.bat tasks --all` |
| Refresh dependency resolution if needed | `.\gradlew.bat --refresh-dependencies help` |

On Linux and macOS, replace `.\gradlew.bat` with `./gradlew`. After switching a version, sync the Gradle project in IntelliJ. Active convenience tasks follow Stonecutter's current selection on the next Gradle invocation.

Production JARs are collected in `build/libs/` with names such as `notesmd-0.1.0+mc1.21.8-fabric.jar`. The mod's semantic version is independent of Minecraft's version. Use the collected JARs for distribution: the older targets require Loom's remapped production output. Development files are kept separately under `versions/<minecraft>/build/`, and each development client uses `run/<minecraft>/`.

## One shared Kotlin codebase

Write the first implementation file yourself under `src/main/kotlin/`, using the package chosen for `mod.group`. Shared assets and metadata live under `src/main/resources/`. `main` is the Gradle source-set name; the entire source set is client-only because Fabric metadata declares `"environment": "client"`.

[Stonecutter](https://github.com/stonecutter-versioning/stonecutter-template-fabric) creates one Gradle project per Minecraft target from the shared `build.gradle.kts`. The root `stonecutter.gradle.kts` controls the active version, future preprocessing rules, formatting, and aggregate tasks. Keep compatibility adjustments small and close to the Minecraft/Fabric integration that needs them; avoid duplicating the entire source tree or creating a Git branch per Minecraft version.

The Loom back compatibility plugin isolates the generation boundary. Minecraft 1.21.11 and older use `net.fabricmc.fabric-loom-remap` with official Mojang mappings and remapped production JARs. Minecraft 26.1+ uses `net.fabricmc.fabric-loom` with the unobfuscated game and no mappings dependency. The shared build uses the plugin's common dependency and artifact APIs. See [development guidance](docs/development.md) and [verified tooling provenance](docs/tooling.md).

## Configuration and IDE

| File | Responsibility |
| --- | --- |
| `stonecutter.properties.toml` | Mod identity, dependency pins, and target-specific Java / Fabric API versions |
| `settings.gradle.kts` | Supported targets and the committed active-version reset point |
| `gradle.properties` | Build-plugin / formatter pins and Gradle behavior |
| `build.gradle.kts` | Shared Kotlin/Fabric build, metadata expansion, validation, and artifact collection |
| `stonecutter.gradle.kts` | Active version, preprocessing, formatting, and aggregate tasks |
| `gradle/wrapper/` | Pinned Gradle distribution and wrapper integrity configuration |

Import the root as a Gradle project in a current IntelliJ IDEA version with Java 25 support. Use the wrapper and JDK 25 as the Gradle JVM. Loom generates client run configurations; the optional Stonecutter IntelliJ plugin improves conditional-comment editing and version switching. Generated IDE configuration and build output are not committed.

To add another Minecraft target, add it to `settings.gradle.kts`, add its exact Java and dependency values to a target section in `stonecutter.properties.toml`, then sync Gradle and validate it. CI reads its matrix from the TOML target sections. [The complete procedure](docs/development.md#adding-a-minecraft-target) includes authoritative version sources and testing requirements.

## Before development and distribution

Replace the obvious `REPLACE_ME` identity values in `stonecutter.properties.toml`: Maven/package group, author, description, contact URLs, and license identifier. Replace `LICENSE` with the chosen license text and `src/main/resources/assets/notesmd/icon.png` with the project's own icon. The current license file is a placeholder and grants no open-source license.

Kotlin `2.4.20` and Fabric Language Kotlin `1.14.1+kotlin.2.4.20` are paired globally. Fabric Loader is pinned globally at `0.19.5`, satisfying the runtime's loader requirement. Fabric API is pinned separately for each exact Minecraft target. Future target-specific overrides belong in the TOML configuration. Fabric Language Kotlin supplies the runtime Kotlin libraries; no application libraries have been chosen.

Mod Menu, mixins, access wideners, publishing plugins, application tests, and feature code are intentionally absent. Add them only when the implementation needs them. This scaffold does not add a Markdown parser, UI framework, config framework, database, or application coroutine infrastructure.

GitHub CI verifies formatting, validates the wrapper, and checks/builds each target. The manually dispatched release-candidate workflow assembles all target JARs for review. Neither workflow publishes a release or needs publishing secrets. Dependabot proposes grouped GitHub Actions updates and supported formatter/toolchain tooling updates; coordinated Minecraft/Fabric/Kotlin changes remain manual because its parser does not cover the central Stonecutter TOML. See [contributing](CONTRIBUTING.md), [releases](docs/releases.md), and [changelog](CHANGELOG.md).
