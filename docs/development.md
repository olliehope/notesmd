# Development

This document describes the infrastructure ready for future Kotlin implementation. It deliberately contains no application source examples. The current repository has no initializer, features, mixins, or tests containing application code.

## Getting started

Install JDK 25 and import the root directory in IntelliJ IDEA as a Gradle project. Select the committed wrapper and JDK 25 for the Gradle JVM. Java 25 starts Gradle for every target; the Java/Kotlin toolchains select Java 21 for 1.21.x or Java 25 for 26.1.2. Foojay can resolve a missing compilation JDK after Gradle starts. See [tooling.md](tooling.md) for the verified versions and their sources.

Run `.\gradlew.bat --version` to verify the launcher, then `.\gradlew.bat :checkAll :buildAll` to verify all targets. Use `./gradlew` on Linux or macOS. Dependency pins are committed; initial resolution requires network access. A source-less build verifies metadata and packaging, but it does not test Notes MD application behavior.

Loom-generated run configurations are preferred to manually maintained IDE files. Run `.\gradlew.bat :runActiveClient` or use the generated client configuration. Each target's development game directory is `run/<minecraft>/`, so worlds and settings do not cross version boundaries. No server configuration is exposed by this client-only scaffold.

## Shared source layout

The single empty `src/main/kotlin/` directory is ready for the owner's first source file. Choose a real package namespace by replacing `mod.group` before adding files. Do not commit generated classes or anything under `versions/<target>/build/`.

The following are possible future areas, not a required package hierarchy or existing implementations:

| Area | Intended responsibility |
| --- | --- |
| `core`, `notes/domain` | Minecraft-independent note model and application rules |
| `markdown`, `storage`, `navigation` | Parsing/rendering decisions, persistence, and note-link behavior |
| `ui`, `platform` | Game screens, Fabric integration, and version compatibility |
| `config`, `util` | Settings or helpers introduced only when a concrete need arises |

Keep independent logic free of Minecraft imports where practical. Use small compatibility sections in the platform integration for real API differences. Create directories as the implementation grows rather than pre-creating placeholder classes, empty interfaces, or abstractions.

`src/main/resources/fabric.mod.json` is a template expanded by Gradle from the centralized properties. The metadata is valid without an entrypoint. When the owner writes a real Kotlin client initializer, add a `client` entrypoint referencing it and set its language adapter to `kotlin`, following the [Fabric Language Kotlin adapter documentation](https://github.com/FabricMC/fabric-language-kotlin#adapter). Keep `"environment": "client"`; do not add `main` or server entrypoints by convention.

## Stonecutter workflow

`settings.gradle.kts` registers one project per supported Minecraft version. All projects evaluate the same `build.gradle.kts`. Root `src/` is the primary codebase; Stonecutter prepares generated variants for the other targets. Kotlin comment preprocessing is available when future compatibility work needs it. Configure shared constants, dependency predicates, and transformations in `stonecutter.gradle.kts`, and follow the syntax for the pinned Stonecutter 0.9 release.

The committed active version and reset point are 1.21.11. In PowerShell:

```powershell
.\gradlew.bat "Set active project to 26.1.2"
.\gradlew.bat :buildActive
.\gradlew.bat :runActiveClient
.\gradlew.bat "Reset active project"
```

Sync Gradle in IntelliJ after switching so the source classpath matches the active version. The optional Stonecutter IntelliJ plugin supports comment editing and the same switch actions. `"Refresh active project"` reprocesses the current state without choosing a different version. Restore the committed active version before committing source edits.

Build/check convenience tasks use the active selection on each Gradle invocation. To inspect or build a target without switching, use its explicit Gradle path, such as `.\gradlew.bat :1.21.5:check :1.21.5:buildAndCollect`. Run `.\gradlew.bat :buildAll` to collect every production JAR, and `.\gradlew.bat :checkAll` for all target checks plus root formatting.

Per-version properties can override shared defaults in `stonecutter.properties.toml`. Keep authored compatibility code in the shared tree. The scaffold ignores `versions/` because it contains generated project state; intentionally adding authored files there would require a reviewed change to the ignore rules. Avoid using duplicate classes at the same shared and local path: active and generated builds may handle such collisions differently. Use shared compatibility conditionals or explicitly selected resources instead of treating source directories as a general replacement overlay system.

## Loom and mappings

The `dev.kikugie.loom-back-compat` plugin handles both generations from the shared script. It selects remapping Loom for the older targets and unobfuscated Loom for 26.1+; it also normalizes dependency configurations and the final artifact task. [The plugin's upstream documentation](https://codeberg.org/KikuGie/loom-back-compat) explains that boundary.

For 1.21.1, 1.21.5, 1.21.8, and 1.21.11, `loomx.applyMojangMappings()` installs official Mojang mappings, and `remapJar` produces the mod's intermediary-namespace release artifact. For 26.1.2, there is no mappings dependency or remapping step, and `jar` is the production artifact. Do not add unconditional mappings or use an older target's plain development JAR for distribution. The collection task selects the correct artifact through `loomx.modJar`.

## Adding a Minecraft target

1. Confirm the exact release in [Mojang's version manifest](https://piston-meta.mojang.com/mc/game/version_manifest_v2.json), and read that release's `javaVersion.majorVersion` field.
2. Check [Fabric Meta](https://meta.fabricmc.net/) for loader support and [Fabric Develop](https://fabricmc.net/develop/) for the corresponding Fabric components. Confirm an exact matching stable Fabric API artifact in [Fabric Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml).
3. Add the release to the `versions(...)` list in `settings.gradle.kts`. Add its matching `"<version>"` section to `stonecutter.properties.toml` with `java.version` and `deps.fabric_api`. Keep the shared Loader/Kotlin runtime pins unless verified compatibility requires a targeted override.
4. Check the release-specific [Fabric porting guidance](https://docs.fabricmc.net/develop/porting/). If the new generation exceeds the pinned Loom, Kotlin, or Gradle support, update the coherent tooling set and its provenance rather than introducing scattered version-era build logic.
5. Sync Gradle, switch to the new target, run its client, and verify relevant application behavior when code exists. Run formatting, `:checkAll`, and `:buildAll`, then restore the committed active version.
6. Update the supported-target tables and changelog. If the default development target changes deliberately, update both `vcsVersion` in settings and Stonecutter's active selection.

GitHub CI reads its build matrix from the TOML target sections, so no separate target list is edited in a workflow. Settings and TOML must describe the same targets. If a future version requires a new JDK major, also update the workflow JDK installation list, the Gradle launcher prerequisite if required, and the wrapper/Kotlin compatibility assessment.

## Optional integration and privileged access

Mod Menu is deliberately not installed. When a real integration needs it, add the [Terraformers Maven repository](https://maven.terraformersmc.com/), choose a compatible `com.terraformersmc:modmenu` version for each target, and centralize those versions in the TOML file. Use a compile-only dependency and, if wanted, a development-runtime dependency through the compatibility plugin's `modCompileOnly` / `modLocalRuntime` configurations. Keep Mod Menu out of required metadata dependencies; a `suggests` entry can advertise it. Add its Kotlin adapter entrypoint only after the real integration exists. Follow the [upstream Mod Menu developer documentation](https://github.com/TerraformersMC/ModMenu#developers).

No mixin configuration or access widener is needed by the inert scaffold. Add a mixin JSON resource under `src/main/resources/` and a client-only metadata mixin declaration only after there are real mixin classes. Choose the target's supported Java compatibility level and confirm the current Loom generation's Mixin behavior; do not copy legacy annotation-processor or refmap configuration without checking it.

If privileged access is needed, put an access widener or class tweaker resource under `src/main/resources/`, configure Loom's `accessWidenerPath`, and reference the packaged path in Fabric metadata. Select or preprocess the resource by target when necessary; use the namespace and format supported by that Minecraft/Loom generation. Stonecutter's `sc.process` can prepare a versioned file for Loom without restructuring the project. Test both older remapped and newer unobfuscated builds. Start from the [current Loom documentation](https://docs.fabricmc.net/develop/loom/) and [official Stonecutter template](https://github.com/stonecutter-versioning/stonecutter-template-fabric), which includes a class-tweaker processing example.

## Formatting and dependency maintenance

Use `.\gradlew.bat :spotlessApply` and `.\gradlew.bat :spotlessCheck`. Shared Kotlin and Gradle DSL use ktlint through Spotless. JSON receives leading-tab conversion and whitespace checks; generated metadata is also parsed during target checks. Markdown and YAML/configuration receive lightweight whitespace checks. No additional JSON formatting library is added. Generated Stonecutter files are outside the targets. Detekt is not added before there is application code or a concrete analysis need.

Stonecutter 0.9.8 emits a warning that the root should not apply `base`. Spotless applies that plugin to supply its lifecycle tasks, so this is an upstream integration warning in the otherwise conventional root formatting setup. The root compiles no application sources, and the warning does not prevent validation or any target build. Initial Loom dependency remapping can also emit upstream Fabric API duplicate-class or mapping-modifier warnings; the validation record distinguishes those from build failures.

Build/plugin pins live in `gradle.properties`; Loom and mod/runtime/target dependencies live in `stonecutter.properties.toml`. Fabric Language Kotlin supplies Kotlin standard/runtime libraries. Its dependency is required in the generated Fabric metadata, and the matching Kotlin compiler version is pinned. Do not independently upgrade one side of that pair without verifying compatibility.

Dependabot proposes weekly grouped GitHub Actions changes and any supported Spotless/Foojay declarations it discovers. Its Gradle parser does not cover the custom Stonecutter TOML, so Minecraft, Fabric components, Kotlin, Loom, and Stonecutter require coordinated manual updates. Do not assume every pin has automated coverage. Confirm source metadata, run all checks/builds, and perform relevant game tests before merging updates. The foundation disables Gradle configuration cache and parallel project execution for reliability across the multi-version graph; reconsider those choices only after verifying the exact updated plugin combination.
