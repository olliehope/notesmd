# Releases

The repository can assemble one production JAR for each supported Minecraft version. No release has been published, and the scaffold has no implemented Notes MD functionality. Release preparation currently produces artifacts for manual review; there are no Modrinth, CurseForge, Maven, or GitHub publication steps.

## Version and artifact identity

Set the Notes MD semantic version in `mod.version` in `stonecutter.properties.toml`. Use versions such as `0.1.0`, `0.2.0`, or `1.0.0`, independently of Minecraft compatibility. All target artifacts for one release share that mod version:

```text
notesmd-0.1.0+mc1.21.1-fabric.jar
notesmd-0.1.0+mc1.21.5-fabric.jar
notesmd-0.1.0+mc1.21.8-fabric.jar
notesmd-0.1.0+mc1.21.11-fabric.jar
notesmd-0.1.0+mc26.1.2-fabric.jar
```

Collected production artifacts live in root `build/libs/`. Use freshly built collected files; development JARs under individual target build directories are not interchangeable with remapped production output. Exact Minecraft compatibility is declared until broader compatibility is tested.

## Preparing a candidate

Before any public distribution, replace all `REPLACE_ME` identity/contact values, choose a license, replace both the `LICENSE` text and metadata license identifier, and replace the placeholder icon. The current license placeholder grants no project license. Ensure real entrypoints and optional integration references correspond to implemented classes, and retain the client-only environment.

Update `CHANGELOG.md` under `Unreleased` with actual changes. When publishing is eventually enabled, move reviewed release notes into a dated version section; do not invent feature history for infrastructure work.

Restore the committed active version and build from a reviewed commit:

```powershell
.\gradlew.bat "Reset active project"
.\gradlew.bat :spotlessCheck :checkAll :buildAll
```

For a fresh artifact directory, use `.\gradlew.bat clean` as a separate command before assembly. Use `./gradlew` on Linux and macOS. Verify that `build/libs/` contains the expected target filenames and inspect each JAR's expanded `fabric.mod.json`, packaged icon, and license. Run the production mod on each supported Minecraft client when actual implementation exists; packaging checks alone do not verify game behavior.

GitHub's **Prepare release candidate** workflow is manually dispatched from the Actions tab. It validates the Gradle Wrapper via `setup-gradle`, installs the configured Java families, verifies formatting and all checks, builds all targets, and uploads the collected JARs as a 30-day CI artifact. Ordinary CI also uploads successful target artifacts with 14-day retention. These artifacts are review outputs, not published releases.

## Publication can be added later

Add publication only when there is a real release destination and project identity. A future workflow may publish the same reviewed candidate files, attach one file per tested Minecraft target, and expose version compatibility accurately. Store tokens in repository secrets and grant only the permissions required by the chosen destination. Ordinary pull-request CI must continue to work without those secrets.

No publishing plugin, release-triggering tag workflow, or credentials are required by the current foundation. Adding them later is a small build/workflow change, not a source-tree redesign. Dependency and tooling updates remain reviewed separately from release approval.
