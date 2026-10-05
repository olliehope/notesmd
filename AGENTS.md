# Repository guidance

Notes MD is a client-only Fabric mod with a shared Kotlin source tree managed by
Stonecutter. Use Gradle Kotlin DSL and the committed wrapper. Run Gradle on Java
25; the target toolchains are configured separately.

The foundation intentionally contains no Kotlin or Java implementation source
files. The repository owner will write the first implementation files. Do not
add dummy initializers, placeholder classes, example code, mixins, or application
tests unless the owner explicitly changes that instruction.

Keep identity and Minecraft dependency pins in `stonecutter.properties.toml`.
Keep build-tool pins in `gradle.properties`. Verify version changes against
official sources and build every supported target. Keep version compatibility
sections small and avoid copied source trees.

Use `:spotlessApply` for formatting, and run `:checkAll :buildAll` before proposing
changes. Generated `versions/`, Gradle caches, and Minecraft run directories do
not belong in Git. Do not publish releases or add publishing credentials as part
of ordinary repository maintenance.
