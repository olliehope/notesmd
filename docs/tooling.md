# Tooling and version provenance

This foundation was researched on **5 October 2026**. The versions below are explicit pins; updating a dependency is a repository change. This document records why the build uses these versions and where to verify them.

## Minecraft targets

All five targets are released Java Edition versions in [Mojang's version manifest](https://piston-meta.mojang.com/mc/game/version_manifest_v2.json). The Fabric API pins are the newest stable releases carrying each exact target suffix in the [official Fabric Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml) at the research date.

| Minecraft | Fabric API | Game and compilation Java | Loom variant | Development names |
| --- | --- | --- | --- | --- |
| 1.21.1 | `0.116.17+1.21.1` | 21 | `net.fabricmc.fabric-loom-remap` | Official Mojang mappings |
| 1.21.5 | `0.128.2+1.21.5` | 21 | `net.fabricmc.fabric-loom-remap` | Official Mojang mappings |
| 1.21.8 | `0.136.1+1.21.8` | 21 | `net.fabricmc.fabric-loom-remap` | Official Mojang mappings |
| 1.21.11 | `0.141.6+1.21.11` | 21 | `net.fabricmc.fabric-loom-remap` | Official Mojang mappings |
| 26.1.2 | `0.155.3+26.1.2` | 25 | `net.fabricmc.fabric-loom` | Unobfuscated game names |

The Java versions come directly from the `javaVersion.majorVersion` fields of Mojang's release metadata: [1.21.1](https://piston-meta.mojang.com/v1/packages/22a1966494dfa4eeb5ee778c8e6ed5b774839582/1.21.1.json), [1.21.5](https://piston-meta.mojang.com/v1/packages/5b1e09e69f4f9c650ba11c36d8b27fd0153e4e82/1.21.5.json), [1.21.8](https://piston-meta.mojang.com/v1/packages/403dee3925f64c7138b72a5302130829cb588784/1.21.8.json), [1.21.11](https://piston-meta.mojang.com/v1/packages/4f6bd9388f12e9d7adc2ded64acba66212d60521/1.21.11.json), and [26.1.2](https://piston-meta.mojang.com/v1/packages/ee044d53f31fdcbfa31be3684ad321d44c81c3a5/26.1.2.json).

## Shared pins

| Component | Selected version | Source and purpose |
| --- | --- | --- |
| Gradle wrapper | `9.7.0` | Matches the [Loom 1.18 wrapper](https://raw.githubusercontent.com/FabricMC/fabric-loom/1.18/gradle/wrapper/gradle-wrapper.properties) and Kotlin's fully supported range. |
| Fabric Loom | `1.18.2` | Latest stable artifact in [Fabric Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml); handles both Loom variants. |
| Fabric Loader | `0.19.5` | Latest stable Loader in the [official Fabric Meta API](https://meta.fabricmc.net/v2/versions/loader). |
| Kotlin JVM plugin | `2.4.20` | Latest stable Kotlin Gradle plugin in [Maven Central metadata](https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/maven-metadata.xml). |
| Fabric Language Kotlin | `1.14.1+kotlin.2.4.20` | Latest matching stable runtime in [Fabric Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-language-kotlin/maven-metadata.xml). |
| Stonecutter | `0.9.8` | Stable version listed by the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/dev.kikugie.stonecutter/0.9.8); manages the shared source tree and target builds. |
| Loom back compatibility plugin | `0.4.2` | Selected stable [published artifact](https://maven.kikugie.dev/releases/dev/kikugie/loom-back-compat/0.4.2/loom-back-compat-0.4.2.pom); switches Loom variants and exposes a common build DSL. |
| Foojay toolchain resolver | `1.0.0` | Stable [Gradle-owned plugin](https://plugins.gradle.org/plugin/org.gradle.toolchains.foojay-resolver-convention/1.0.0) for resolving compilation JDKs. |
| Spotless | `8.10.3` | Stable [Gradle plugin](https://plugins.gradle.org/plugin/com.diffplug.spotless/8.10.3) for formatting checks and fixes. |
| ktlint | `1.8.0` | Stable [upstream release](https://github.com/ktlint/ktlint/releases/tag/1.8.0) used by Spotless. |

Loom back compatibility `0.4.2` is an intentional selected pin. Its [release metadata](https://maven.kikugie.dev/releases/dev/kikugie/loom-back-compat/maven-metadata.xml) also lists `0.4.3`; this foundation does not claim `0.4.2` is the newest release.

## Gradle and Java

**Run the Gradle wrapper with JDK 25.** Loom `1.18.2` publishes a Java 25 runtime variant with Gradle plugin API version `9.7.0`, as shown in its [Gradle module metadata](https://maven.fabricmc.net/net/fabricmc/fabric-loom/1.18.2/fabric-loom-1.18.2.module). The [Loom 1.18 release](https://github.com/FabricMC/fabric-loom/releases/tag/1.18) also describes its use of Java 25's Foreign Function and Memory API. Set the IDE's Gradle JVM and the terminal's `JAVA_HOME` accordingly.

The Gradle launcher JVM and each mod's compilation target have different roles. Gradle runs on Java 25 for every target. The `1.21.x` builds compile Java and Kotlin to Java 21; `26.1.2` compiles to Java 25. Matching both language targets prevents JVM target mismatches and keeps the older mod artifacts usable by Java 21 game installations. Foojay can provision missing compilation toolchains after Gradle has started; it does not supply the JVM needed to start the wrapper. See [Gradle's toolchain documentation](https://docs.gradle.org/current/userguide/toolchains.html).

Gradle `9.8.0` was the newest stable release at the research date, according to the [Gradle release API](https://services.gradle.org/versions/current). The wrapper deliberately uses `9.7.0`: this is Loom 1.18's own wrapper version and the maximum fully supported Gradle version documented for [Kotlin `2.4.20`](https://kotlinlang.org/docs/gradle-configure-project.html). This choice avoids moving beyond the documented compatibility range merely to select the newest wrapper.

The official [Gradle `9.7.0` binary distribution checksum](https://services.gradle.org/distributions/gradle-9.7.0-bin.zip.sha256) is:

```text
84fbba45c7f4c64abc77460e1c00f541e9f960e3c7ed2538f1ede19eacd873ae
```

## Kotlin runtime strategy

Use Kotlin `2.4.20`, Fabric Language Kotlin `1.14.1+kotlin.2.4.20`, and Loader `0.19.5` for all five Minecraft targets. Fabric Language Kotlin is the separately installed runtime dependency that provides the `kotlin` language adapter and Kotlin libraries for entrypoints. It is independent of the Fabric API's target-specific version.

The matching [Fabric Language Kotlin POM](https://maven.fabricmc.net/net/fabricmc/fabric-language-kotlin/1.14.1+kotlin.2.4.20/fabric-language-kotlin-1.14.1+kotlin.2.4.20.pom) declares Kotlin standard library and reflection `2.4.20`. The published [runtime JAR](https://maven.fabricmc.net/net/fabricmc/fabric-language-kotlin/1.14.1+kotlin.2.4.20/fabric-language-kotlin-1.14.1+kotlin.2.4.20.jar) was inspected: its `fabric.mod.json` requires `fabricloader >=0.19.5`, and its adapter classes use Java 8 bytecode. Therefore the Loader floor must stay at least `0.19.5` with this runtime pin; the adapter bytecode supports the Java 21 and Java 25 game targets. This is artifact inspection, rather than an inference from the version suffix alone.

## Mappings and packaged artifacts

For Minecraft `1.21.1` through `1.21.11`, use `loom.officialMojangMappings()` for development. These game releases are obfuscated, so `net.fabricmc.fabric-loom-remap` remaps the built mod into Fabric's production intermediary namespace. The distributable task is `remapJar`; the plain development `jar` is not the production mod artifact.

For Minecraft `26.1.2`, use `net.fabricmc.fabric-loom` and omit the mappings dependency. The game is unobfuscated, so the distributable task is `jar`. The plugin split and output behavior are documented in the [official Loom guide](https://docs.fabricmc.net/develop/loom/), and the removal of the mappings dependency is documented in [Fabric's 26.1.2 porting guide](https://docs.fabricmc.net/26.1.2/develop/porting/).

The Loom back compatibility plugin gives the shared build a common dependency DSL and selects the appropriate production JAR task. This keeps one build script usable across the obfuscated and unobfuscated targets. Its behavior can be checked against the [published `0.4.2` source artifact](https://maven.kikugie.dev/releases/dev/kikugie/loom-back-compat/0.4.2/loom-back-compat-0.4.2-sources.jar).

## Reproducibility scope

Explicit version pins fix the repository's declared Minecraft, mod dependency, build plugin, and formatter versions. A wrapper distribution checksum checks the downloaded Gradle distribution against the pinned checksum. [Gradle dependency lockfiles](https://docs.gradle.org/current/userguide/dependency_locking.html), when generated and committed, additionally record resolved module versions, including transitive dependencies. [Dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html) checks artifact integrity when verification metadata is supplied.

Version pins and dependency locks do not by themselves promise identical JAR bytes on every machine or verify runtime behavior in Minecraft. They also do not freeze the JDK patch selected by a resolver that requests only a major Java version. Claims about successful builds, packaging, and game launches belong in the actual validation results, rather than being implied by this version table.
