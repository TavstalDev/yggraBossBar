# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [0.1.2] - 2026-11-23

### Added

- Sources and Javadoc JARs for the API module, published alongside the shaded Maven artifact.

### Changed

- Bumped the Gradle wrapper to 9.8.2.
- Configured Dependabot to ignore Spigot/Paper API and Gradle/Shadow dependency updates.

## [0.1.1] - 2026-11-17

### Added

- Maven publishing for the shaded library JAR.
- JitPack publishing, with Gradle (Kotlin and Groovy DSL) and Maven consumption examples in the
  README.

### Changed

- Migrated Java packages and Maven coordinates from `io.github.tavstaldev` to
  `com.github.TavstalDev` to match the JitPack group ID. This is a breaking change for imports and
  dependency declarations.
- Simplified the root Gradle build configuration.

## [0.1.0] - 2026-11-13

Initial release.

### Added

- Version-independent `IBossBar` API for creating and managing boss bars by identifier.
- Show and hide operations for a single player or a list of players.
- Title and progress updates for visible bars, plus `hideAll` and `clearAll` lifecycle methods.
- Runtime implementation selection for:
  - Minecraft 1.8 – 1.9.3, using an invisible wither entity and NMS packets.
  - Minecraft 1.9.4 – 1.12.2, using the Bukkit boss bar API.
  - Minecraft 1.13 – 1.16.4, using the Adventure platform for Bukkit.
  - Minecraft 1.16.5 and newer, using Paper's Adventure boss bar API.
- Graceful no-op fallback with a logged warning when the server version is unsupported or an
  implementation cannot be loaded.
- `AdventureUtils` helpers for legacy color-code and MiniMessage parsing, plus plain text,
  MiniMessage, legacy and JSON serialization.
- Single shaded library JAR containing the API and every implementation.
- Example plugin demonstrating Adventure loading with Libby and a basic join boss bar.
- Gradle multi-module build targeting Java 8 for all published modules, with a JDK 25 toolchain.
- GitHub Actions release workflow for building the library and publishing a pre-release.

[Unreleased]: https://github.com/TavstalDev/yggraBossBar/compare/v0.1.2...HEAD
[0.1.2]: https://github.com/TavstalDev/yggraBossBar/releases/tag/v0.1.2
[0.1.1]: https://github.com/TavstalDev/yggraBossBar/releases/tag/v0.1.1
[0.1.0]: https://github.com/TavstalDev/yggraBossBar/releases/tag/v0.1.0
