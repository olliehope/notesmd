# GitHub setup

Notes MD is hosted at [olliehope/notesmd](https://github.com/olliehope/notesmd) as a public repository, with `origin` configured in the local checkout at `P:\notesmd`.

Project identity is set to Ollie Hope and `io.github.olliehope.notesmd`. Notes MD uses the [MIT license](../LICENSE); the Gradle Wrapper retains its upstream [Apache-2.0 license](../gradle/wrapper/LICENSE). The project remains infrastructure only, with no Kotlin or Java implementation files.

The [CI workflow](https://github.com/olliehope/notesmd/actions/workflows/ci.yml) verifies formatting and builds every configured Minecraft target. Its stable `CI passed` check covers target discovery, formatting, and all matrix builds, including failures or skipped prerequisites. The [release-candidate workflow](https://github.com/olliehope/notesmd/actions/workflows/release-candidate.yml) assembles review artifacts without publishing a release.

Repository settings and hosted verification are being completed during the initial upload. This record will be updated with the verified settings and run results.
