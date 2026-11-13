# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[0.1.0]: https://github.com/TavstalDev/yggraBossBar/releases/tag/v0.1.0
