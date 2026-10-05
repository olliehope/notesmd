# GitHub setup

Notes MD is hosted at [olliehope/notesmd](https://github.com/olliehope/notesmd) as a public repository, with `origin` configured in the local checkout at `P:\notesmd`.

Project identity is set to Ollie Hope and `io.github.olliehope.notesmd`. Notes MD uses the [MIT license](../LICENSE); the Gradle Wrapper retains its upstream [Apache-2.0 license](../gradle/wrapper/LICENSE). The project remains infrastructure only, with no Kotlin or Java implementation files.

The [CI workflow](https://github.com/olliehope/notesmd/actions/workflows/ci.yml) verifies formatting and builds every configured Minecraft target. Its stable `CI passed` check covers target discovery, formatting, and all matrix builds, including failures or skipped prerequisites. The [release-candidate workflow](https://github.com/olliehope/notesmd/actions/workflows/release-candidate.yml) assembles review artifacts without publishing a release.

## Repository settings

The verified default branch is `main`. Issues are enabled; Wiki and Projects are disabled because project documentation lives in the repository. Pull requests use squash merges with their title and description as the commit message. Merge commits and rebase merges are disabled. Merged branches are deleted automatically, branch updates are enabled, and auto-merge is available when checks pass.

`main` requires pull requests, the up-to-date `CI passed` check from GitHub Actions, resolved review conversations, and linear history. Force pushes and branch deletion are disabled. No approving review is required, so a solo owner is not blocked by an inability to approve their own pull request. The owner retains GitHub's administrator bypass; normal contributions use pull requests.

Actions are enabled and the repository requires actions to be pinned to full commit SHAs. Workflow tokens default to read access and cannot approve pull requests. Dependabot alerts and security updates, secret scanning, and secret-scanning push protection are enabled. CodeQL is deferred until implementation code exists.

## Hosted verification

The [initial CI run](https://github.com/olliehope/notesmd/actions/runs/37293709387) passed all eight jobs: target discovery, formatting, five Minecraft builds, and `CI passed`. It uploaded five production JAR artifacts. Those Linux-built artifacts were downloaded and their SHA-256 hashes matched the local Windows build for every target, using the configured GitHub identity and MIT metadata.

The [release-candidate verification](https://github.com/olliehope/notesmd/actions/runs/37293953847) passed on attempt 2 and uploaded [one candidate archive containing all five JARs](https://github.com/olliehope/notesmd/actions/runs/37293953847/artifacts/11338366397). The first attempt failed during plugin resolution because the build-tool Maven endpoint did not respond; retrying the same configuration resolved the failure. No dependencies or source files were changed to obtain a passing build.

No mod releases, Maven publications, Modrinth/CurseForge uploads, publishing credentials, or implementation source files have been created.
