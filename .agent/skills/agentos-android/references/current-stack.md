# Current Android stack snapshot

Checked 2026-08-11. Treat this as a dated compatibility snapshot, not permanent truth.

## Verified upstream guidance

- AndroidX stable releases: Activity 1.13.0, Core 1.18.0, Lifecycle 2.11.0, Navigation 2.9.8, Camera 1.6.0, WorkManager 2.11.2, Datastore 1.2.1.
- Compose BOM: 2026.06.00.
- Kotlin: 2.4.10.
- KSP: 2.3.10 is the version shown by the current Kotlin KSP quickstart alongside Kotlin 2.4.10.
- AGP 9.1.1 supports API 37 and requires Gradle 9.3.1 and JDK 17. AGP 9.3 requires Gradle 9.5.0 or newer.

## Important compatibility notes

- Compose 1.12.x requires compileSdk 37 and AGP 9+.
- Compose libraries should be versioned through the Compose BOM.
- AndroidX versions move independently; always re-check the official release index before upgrades.
- Do not assume the numerically newest alpha is preferable to the latest stable release.

## Official sources

- https://developer.android.com/jetpack/androidx/versions
- https://developer.android.com/develop/ui/compose/bom
- https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler
- https://developer.android.com/build/releases/about-agp
- https://developer.android.com/build/releases/agp-9-1-0-release-notes
- https://kotlinlang.org/docs/releases.html
- https://kotlinlang.org/docs/ksp-quickstart.html
- https://github.com/android/skills

## Update rule

When a build or migration needs a newer component, verify the release and compatibility requirements first, then change the version catalog and this dated snapshot in the same change.
