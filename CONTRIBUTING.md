# Contributing to Notes MD

Notes MD is a client-only, Fabric-only Kotlin project. The repository begins as infrastructure only; its first application source files are to be written by the project owner. Keep future changes focused on one problem and explain the resulting behavior in the pull request.

## Development expectations

- Use Kotlin for implementation, with shared sources under `src/main/kotlin/`.
- Keep Minecraft-independent logic separate from Fabric and game integration where that helps testing and compatibility.
- Isolate version differences instead of copying features into a source tree for every Minecraft target.
- Preserve the client-only metadata and add only entrypoints that reference real code.
- Prefer dependencies that solve an actual implementation need. Discuss substantial new frameworks before adding them.
- Use clear commit messages and feature branches from `main`; pull requests are the normal review path.

Use JDK 25 to run the Gradle wrapper. Each supported target has its own compilation/game toolchain. Follow [development.md](docs/development.md) for IntelliJ import, version switching, and adding target versions.

## Before opening a pull request

Run these commands from the repository root in PowerShell:

```powershell
.\gradlew.bat "Reset active project"
.\gradlew.bat :spotlessApply
.\gradlew.bat :checkAll :buildAll
```

On Linux and macOS use `./gradlew`. Resetting restores the agreed active version, 1.21.11, and processes shared compatibility comments for that version. Review the resulting diff before committing.

Spotless checks shared Kotlin sources, Gradle Kotlin DSL, metadata/configuration, and documentation. Generated Stonecutter sources and target build output are excluded. Formatting should pass without unrelated source churn. When implementation exists, add useful tests for behavior and run development clients on versions affected by game/API changes; successful compilation alone does not verify runtime compatibility.

All supported targets must continue to build unless a deliberate support-policy change is reviewed and documented. When changing target versions or dependencies, update the central configuration and relevant documentation together. Do not silently substitute or remove a requested Minecraft target to work around a failure.

## Pull requests and issues

Describe the concrete change, its reason, and the checks performed. For compatibility fixes, identify the Minecraft versions tested. Avoid committing `.idea/`, run directories, logs, caches, generated source trees, or secrets. Keep the Gradle Wrapper files and intended infrastructure configuration committed.

GitHub requires the `CI passed` check on pull requests into `main`. It covers formatting and every target build; update the branch when the base changes and resolve review conversations before merging. No external approving review is mandatory for this solo-owner repository. Squash merges are the normal merge method.

For bugs, include the exact Minecraft, Fabric Loader, and Notes MD versions, reproduction steps, and relevant logs. Remove tokens and personal information from logs before sharing them. Report infrastructure-only issues as such while the scaffold has no implemented functionality.

Record user-visible changes in the `Unreleased` section of [CHANGELOG.md](CHANGELOG.md). There is no released feature history yet. Release preparation is documented in [docs/releases.md](docs/releases.md).
