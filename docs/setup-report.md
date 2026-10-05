# Notes MD foundation report

Prepared on **5 October 2026**. Repository: `P:\notesmd`. Mod ID: `notesmd`; display name: **Notes MD**; initial mod version: `0.1.0`.

The client-only, Fabric-only, Kotlin-first foundation is configured and all five requested target checks and builds pass. It contains **zero `.kt` or `.java` implementation files**, and the produced scaffold JARs contain no implementation classes. Metadata has no entrypoints or mixin references, so these are valid inert artifacts rather than a working notes application. The owner will write the first source file.

## Repository tree and Git state

The authored/committed foundation has the following layout; this report is the additional documentation file. Generated project directories, caches, and output are ignored.

```text
notesmd/
├── .editorconfig
├── .gitattributes
├── .gitignore
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug.yml
│   │   └── config.yml
│   ├── dependabot.yml
│   ├── pull_request_template.md
│   └── workflows/
│       ├── ci.yml
│       └── release-candidate.yml
├── AGENTS.md
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
├── README.md
├── build.gradle.kts
├── docs/
│   ├── development.md
│   ├── releases.md
│   ├── setup-report.md
│   └── tooling.md
├── gradle.properties
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── stonecutter.gradle.kts
├── stonecutter.properties.toml
└── src/main/
    ├── kotlin/.gitkeep
    └── resources/
        ├── fabric.mod.json
        └── assets/notesmd/icon.png
```

Git is initialized on `main`, with foundation commit `19c71aa` (`Set up Kotlin Fabric multi-version foundation`) and no remotes. The wrapper scripts, JAR, properties, metadata, and intended configuration are committed. The Unix wrapper has executable Git mode `100755`. No remote branches, publication, or external repository settings have been created.

Generated local directories include `versions/<minecraft>/`, per-target builds, `.gradle/`, `run/<minecraft>/`, and collected production files in `build/libs/`.

## Plugins and build tooling

| Plugin / component | Pin | Purpose |
| --- | --- | --- |
| `dev.kikugie.stonecutter` | `0.9.8` | Defines the five target projects, shares sources/build logic, and provides preprocessing and version switching |
| `dev.kikugie.loom-back-compat` | `0.4.2` | Selects the correct Loom variant and normalizes dependency/artifact APIs across the 26.1 boundary |
| `net.fabricmc.fabric-loom-remap` / `net.fabricmc.fabric-loom` | `1.18.2` | Applied through back compatibility; supplies Minecraft development, client runs, and production packaging |
| `org.jetbrains.kotlin.jvm` | `2.4.20` | Configures Kotlin compilation and target JVM versions |
| `org.gradle.toolchains.foojay-resolver-convention` | `1.0.0` | Resolves missing compilation/game JDK toolchains |
| `com.diffplug.spotless` | `8.10.3` | Applies/verifies authored-source and repository formatting |
| ktlint, through Spotless | `1.8.0` | Formats Kotlin source and Gradle Kotlin DSL; a build-only tool |
| Gradle Wrapper | `9.7.0` | Pins the supported Gradle launcher/distribution |

Loom also applies Gradle's built-in Java/Eclipse-related plugins, and Spotless applies the root base lifecycle plugin. These inherited plugins have no independent version pin and are tooling integrations, not application dependencies. No convention-plugin framework or publishing plugin is added. Versions and their authoritative evidence are recorded in [tooling.md](tooling.md).

Gradle uses UTF-8, a 3 GiB maximum heap, daemon/build cache, and four workers. Parallel project execution and configuration cache are disabled for reliability with the multi-version plugin graph. Java/Kotlin compilation targets are aligned. JSON formatting uses lightweight whitespace normalization; metadata validation uses Groovy supplied by Gradle, with no added Gson/Jackson application dependency.

## Dependencies, targets, and Java

| Minecraft dependency | Fabric API dependency | Compilation and game Java | Loom / mappings |
| --- | --- | --- | --- |
| `com.mojang:minecraft:1.21.1` | `net.fabricmc.fabric-api:fabric-api:0.116.17+1.21.1` | 21 | Remapping Loom; official Mojang mappings |
| `com.mojang:minecraft:1.21.5` | `net.fabricmc.fabric-api:fabric-api:0.128.2+1.21.5` | 21 | Remapping Loom; official Mojang mappings |
| `com.mojang:minecraft:1.21.8` | `net.fabricmc.fabric-api:fabric-api:0.136.1+1.21.8` | 21 | Remapping Loom; official Mojang mappings |
| `com.mojang:minecraft:1.21.11` | `net.fabricmc.fabric-api:fabric-api:0.141.6+1.21.11` | 21 | Remapping Loom; official Mojang mappings |
| `com.mojang:minecraft:26.1.2` | `net.fabricmc.fabric-api:fabric-api:0.155.3+26.1.2` | 25 | Unobfuscated Loom; no mappings dependency |

Each target also directly declares:

- `net.fabricmc:fabric-loader:0.19.5`, the Fabric loader/runtime foundation.
- `net.fabricmc:fabric-language-kotlin:1.14.1+kotlin.2.4.20`, the Kotlin adapter and shared runtime libraries. It is required in the generated metadata.

The table and list above cover the declared Minecraft/mod dependencies. Loom additionally resolves the generation-appropriate mappings/intermediary data, Minecraft libraries, and Mixin tooling; the build plugins and Fabric Language Kotlin have their normal transitive dependencies. These required tooling/runtime components are separate from adding application libraries.

The complete Fabric API is included per target as the conventional Fabric integration foundation, without selecting arbitrary individual modules. Fabric API versions follow the exact Minecraft release; Loader and Fabric Language Kotlin are global pins with room for explicit per-target TOML overrides. Kotlin compiler/runtime versions are paired at `2.4.20`; Kotlin's automatic separate standard-library dependency is disabled because Fabric Language Kotlin supplies those libraries at runtime. The runtime artifact's metadata and bytecode were inspected to verify its Loader floor and Java compatibility, rather than assuming compatibility from its version suffix.

**JDK 25 runs Gradle for every target.** Foojay can provision missing Java 21/25 target JDKs after the wrapper starts. It does not replace the JDK 25 launcher prerequisite. The toolchain and client run configuration inspections confirmed Java 21 for the older targets and Java 25 for 26.1.2.

## Stonecutter, mappings, and artifacts

`settings.gradle.kts` registers all five exact targets, with **1.21.11** as the committed active/reset version. Identity and dependencies live in `stonecutter.properties.toml`; tooling pins and Gradle behavior live in `gradle.properties`. Each target evaluates the single shared `build.gradle.kts`; authored Kotlin will go under `src/main/kotlin/`. Stonecutter preprocessing rules and aggregate tasks are grouped in `stonecutter.gradle.kts`.

The back compatibility plugin centralizes the Loom split. Before 26.1, `loomx.applyMojangMappings()` installs official Mojang mappings and remapping Loom produces the production intermediary-namespace JAR through `remapJar`. For 26.1.2, the game is unobfuscated, no mappings dependency is installed, and `jar` is the production artifact. Inspection confirmed one mappings dependency on older targets and zero on 26.1.2.

`loomx.modJar` selects the correct production task. Its filename is explicitly pinned to `notesmd-<mod-version>+mc<minecraft-version>-fabric.jar`, independently of the mod's semantic version. `buildAll` collects the five production JARs in root `build/libs/`. Target resources expand centralized metadata, declare exact Minecraft compatibility and client-only environment, and include the placeholder icon and license. JAR ordering/timestamps are normalized.

## Commands

Run these in PowerShell from `P:\notesmd`; replace `.\gradlew.bat` with `./gradlew` on Linux/macOS.

| Action | Command |
| --- | --- |
| Verify wrapper / launcher JVM | `.\gradlew.bat --version` |
| Build the active target | `.\gradlew.bat :buildActive` |
| Build every target | `.\gradlew.bat :buildAll` |
| Build a particular target | `.\gradlew.bat :1.21.8:buildAndCollect` |
| Run an active development client | `.\gradlew.bat :runActiveClient` |
| Apply formatting | `.\gradlew.bat :spotlessApply` |
| Verify formatting | `.\gradlew.bat :spotlessCheck` |
| Check the active target | `.\gradlew.bat :checkActive` |
| Check all targets and formatting | `.\gradlew.bat :checkAll` |
| Switch active Minecraft | `.\gradlew.bat "Set active project to 26.1.2"` |
| Restore committed active version | `.\gradlew.bat "Reset active project"` |
| Reprocess current shared sources | `.\gradlew.bat "Refresh active project"` |
| Inspect tasks / refresh dependency resolution | `.\gradlew.bat tasks --all` / `.\gradlew.bat --refresh-dependencies help` |

Sync the Gradle project in IntelliJ after switching. Generated client run configurations are used; machine-specific IDE files are ignored. Add future targets to the settings list and a matching TOML section with verified Java/Fabric API versions. CI derives its matrix from TOML, so there is no separate Minecraft target list in a workflow. A new Java generation can also require updating the workflow JDK list and verifying the Gradle/Kotlin/Loom combination. See [development.md](development.md).

## CI, updates, and release foundations

CI runs on `main` pushes, pull requests, and manual dispatch. It validates the wrapper through `setup-gradle`, checks formatting, installs Java 21/25, checks/builds all target matrix jobs without skipping failures, and uploads production JARs. Actions use pinned revisions, minimal read permissions, and no publishing secrets. Hosted GitHub CI has not been executed because there is no remote repository.

Dependabot groups weekly Actions updates and supported Spotless/Foojay Gradle updates. The central custom Stonecutter TOML is outside its Gradle parser's coverage, so Minecraft/Fabric/Kotlin/Loom/Stonecutter upgrades require coordinated manual verification. No automatic target replacement or compatibility upgrade is configured.

The manually dispatched release-candidate workflow verifies/builds every target and uploads review artifacts. It does not publish anything. Semantic versioning, an `Unreleased` changelog, and [release instructions](releases.md) are prepared; there is no fake release history or required Modrinth/CurseForge credential.

## Replacements, omissions, and assumptions

Before public distribution, replace the TOML's Maven/package group, author, description, homepage/sources/issues URLs, and license identifier; replace the license placeholder with a real license and the placeholder PNG with a project icon. The current license file grants no project license. The mod ID/name and initial semantic version follow the request, and exact game compatibility is deliberately conservative.

No Mod Menu dependency/integration, Detekt, publication plugins, mixin config/classes, access widener/class tweaker, initializer, application tests, Markdown parser, config/UI framework, database, or other application library is present. These require actual implementation needs or a publication destination. Fabric Language Kotlin's normal bundled runtime libraries do not introduce application coroutine infrastructure. Optional future setup is documented without source examples or nonexistent entrypoint references.

The source layout is intentionally empty and small. Future core/domain/storage/navigation logic can stay mostly Minecraft-independent, with UI/platform integration and compatibility sections introduced as needed. No branch-per-Minecraft-version workflow or copied source tree is created. Feature branches and pull requests from `main` are the documented Git workflow.

## Validation and remaining limits

- Wrapper `9.7.0` starts successfully, and the official distribution SHA-256 was verified: `84fbba45c7f4c64abc77460e1c00f541e9f960e3c7ed2538f1ede19eacd873ae`.
- Formatting verification, all configured target checks, and all five production builds pass. Expanded metadata/icon/license packaging and production filenames were inspected.
- Kotlin source wiring includes the shared/generated Stonecutter paths correctly. Client JVM selection and the older/newer mappings distinction were inspected.
- Switching to 26.1.2, dry-running `runActiveClient`, and resetting to 1.21.11 succeed. An actual Minecraft UI was not launched.
- Kotlin/Java compilation and application-test tasks report `NO-SOURCE` as expected. There are no Kotlin/Java implementation files or packaged implementation classes. The source-less scaffold builds without adding dummy code.
- An isolated local clone of foundation commit `19c71aa`, with no project caches, passes `:spotlessCheck :checkAll :buildAll`. A second fresh packaging run using `:clean :checkAll :buildAll --no-build-cache --rerun-tasks` also passes with 52 executed tasks. Independent JAR inspection finds no implementation files/classes, and all five production SHA-256 hashes match the original build.

Known integration debt is limited to upstream warnings: Stonecutter 0.9.8 warns about the root `base` plugin applied by Spotless; initial Loom remapping can emit Fabric API duplicate-class/mapping-modifier warnings. They do not fail these checks/builds, and no custom workaround is added.

The isolated rebuild reused global dependency-download caches and installed JDKs 21.0.12/25.0.4; it did not use an empty Gradle user home. Matching hashes demonstrate practical reproducibility on this machine and toolchain combination. There are no dependency lockfiles, and toolchain resolution pins the Java major rather than a specific patch. Declared pins and normalized JAR settings do not guarantee identical bytes across operating systems or future JDK patches. Runtime compatibility still needs testing once real implementation exists.
