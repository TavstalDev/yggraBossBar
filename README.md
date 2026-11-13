# yggraBossBar

![Release (latest by date)](https://img.shields.io/github/v/release/TavstalDev/yggraBossBar?style=plastic-square)
![Workflow Status](https://img.shields.io/github/actions/workflow/status/TavstalDev/yggraBossBar/release.yml?branch=stable&label=build&style=plastic-square)
![License](https://img.shields.io/github/license/TavstalDev/yggraBossBar?style=plastic-square)
![Downloads](https://img.shields.io/github/downloads/TavstalDev/yggraBossBar/total?style=plastic-square)
![Stars](https://img.shields.io/github/stars/TavstalDev/yggraBossBar?style=plastic-square)
![Issues](https://img.shields.io/github/issues/TavstalDev/yggraBossBar?style=plastic-square)
![Forks](https://img.shields.io/github/forks/TavstalDev/yggraBossBar?style=plastic-square)

A small, version-independent **boss bar** library for Spigot/Paper plugins. It picks the right
implementation for the running server at runtime, so your plugin does not need its own NMS or
version-specific code.

## Features

- One API for Minecraft **1.8 through 26.2+**.
- Manage any number of boss bars by identifier.
- Show or hide a bar for a single player or a list of players.
- Update a bar's title and progress while it is visible.
- Legacy `&`/`§` color codes, plus MiniMessage on Adventure-based versions.
- Automatic server version detection with a graceful fallback when no implementation is available.
- A single shaded JAR that contains every supported implementation.
- No need to shade Adventure: use the server-provided copy or load it at runtime.
- Ready-to-use example plugin for manual testing.

## Supported versions

| Minecraft version | Implementation | How it works |
| --- | --- | --- |
| 1.16.5+ | `impl_v1_16_5` | Paper's Adventure boss bar API (`Player#showBossBar`) |
| 1.13 – 1.16.4 | `impl_v1_13` | Adventure platform for Bukkit (`BukkitAudiences`) |
| 1.9.4 – 1.12.2 | `impl_v1_9_4` | Bukkit boss bar API (`Bukkit#createBossBar`) |
| 1.8 – 1.9.3 | `impl_v1_8_8` | Invisible wither entity driven by NMS packets |

> **Paper is required on 1.16.5+.** The implementation uses the Adventure `Audience` methods that
> Paper adds to `Player`; plain Spigot does not provide them. The 1.8 – 1.16.4 implementations work
> on both Spigot and Paper.

The implementation is selected once, when `YggraBossBar` is constructed. If the server version is
not supported, or the implementation cannot be loaded, a warning is logged and calls are ignored
instead of crashing your plugin.

### Title formatting

| Versions | Supported formatting |
| --- | --- |
| 1.13+ | Legacy `&`/`§` color codes and MiniMessage |
| 1.9.4 – 1.12.2 | Legacy `§` color codes only |
| 1.8 – 1.9.3 | Legacy `&`/`§` color codes |

## Project structure

| Module | Purpose |
| --- | --- |
| `api` | The public `IBossBar` interface and internal `AdventureUtils` helpers |
| `bootstrap` | Version detection and the `YggraBossBar` facade |
| `implementation/impl_v1_8_8` | 1.8 – 1.9.3 implementation |
| `implementation/impl_v1_9_4` | 1.9.4 – 1.12.2 implementation |
| `implementation/impl_v1_13` | 1.13 – 1.16.4 implementation |
| `implementation/impl_v1_16_5` | 1.16.5+ implementation |
| `plugin-test` | Example plugin showing Libby runtime loading |

## Requirements

- A Spigot or Paper server running Minecraft **1.8 or newer**.
  **Paper is required for 1.16.5+** (see above).
- **Java 8+** at runtime. All published modules target Java 8.
- **JDK 25** with the included Gradle wrapper to build the project.
- Adventure, depending on the target version:
  - **Paper 1.18+** provides Adventure and MiniMessage; nothing extra is needed.
  - **Paper 1.16.5 – 1.17.x** provides Adventure but not MiniMessage, which the title parser
    uses. Provide a compatible MiniMessage artifact or target a newer Paper build.
  - **1.13 – 1.16.4** needs `adventure-platform-bukkit` and its runtime dependencies.
  - **1.8 – 1.12.2** does not use Adventure at runtime.

## Building from source

```bash
./gradlew build
```

The library JAR is written to:

```text
bootstrap/build/libs/yggraBossBar-0.1.0.jar
```

This JAR already contains the API and all version-specific implementations. Adventure is not
bundled; it is provided by the server or loaded at runtime (see [Usage](#usage)).

To build the example plugin:

```bash
./gradlew :plugin-test:shadowJar
```

The result is `plugin-test/build/libs/yggraBossBar-test-1.0.0.jar`.

## Usage

### 1. Add the library to your plugin

Shade the produced library JAR into your plugin. If you build this repository as part of your
project, you can depend on the shadow configuration directly:

```kotlin
dependencies {
    implementation(project(":bootstrap", configuration = "shadow")) {
        isTransitive = false
    }
}
```

If you use the pre-built JAR, add it as a local dependency:

```kotlin
dependencies {
    implementation(files("libs/yggraBossBar-0.1.0.jar"))
}
```

### 2. Provide Adventure

Do **not** bundle Adventure inside the library JAR. Declare the Adventure artifacts as
`compileOnly`; whether they are needed at runtime depends on the server version.

```kotlin
dependencies {
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")
    // Only needed when you target Minecraft 1.13 - 1.16.4.
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
}
```

On **Paper 1.18+** the server already provides these classes, so no runtime loading is required.
On **1.13 – 1.16.4**, load or shade `adventure-platform-bukkit` together with its runtime
dependencies. On **1.8 – 1.12.2**, the implementation does not touch Adventure at all.

If you load Adventure at runtime with a dependency manager such as
[Libby](https://github.com/AlessioDP/libby), remember that Libby resolves each artifact
individually and does **not** pull transitive dependencies. Load every artifact your target
version needs, and add `adventure-platform-bukkit` plus its runtime dependencies when you support
1.13 – 1.16.4. The [`plugin-test`](plugin-test) module contains a working Libby setup you can use
as a starting point. Its base Adventure loader looks like this; add the platform artifacts when
you need 1.13 – 1.16.4 support.

```java
import io.github.tavstaldev.yggraBossBar.api.IBossBar;
import io.github.tavstaldev.yggraBossBar.bootstrap.YggraBossBar;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    private IBossBar bossBar;

    private static final String[][] ADVENTURE_LIBRARIES = {
            {"examination-api", "1.3.0"},
            {"examination-string", "1.3.0"},
            {"adventure-key", "4.26.1"},
            {"adventure-api", "4.26.1"},
            {"adventure-text-serializer-legacy", "4.26.1"},
            {"adventure-text-minimessage", "4.26.1"},
            {"adventure-text-serializer-plain", "4.26.1"},
            {"adventure-text-serializer-gson", "4.26.1"}
    };

    @Override
    public void onEnable() {
        BukkitLibraryManager libraries = new BukkitLibraryManager(this);
        libraries.addMavenCentral();
        libraries.addSonatype();
        libraries.addRepository("https://jitpack.io");

        for (String[] artifact : ADVENTURE_LIBRARIES) {
            libraries.loadLibrary(Library.builder()
                    // "{}" prevents the string from being rewritten during relocation.
                    .groupId("net{}kyori")
                    .artifactId(artifact[0])
                    .version(artifact[1])
                    .build());
        }

        bossBar = new YggraBossBar(this);
    }
}
```

### 3. Create the boss bar facade

Create one `YggraBossBar` instance during `onEnable`, after Adventure is available, and reuse it
for the lifetime of the plugin:

```java
bossBar = new YggraBossBar(this);
```

### 4. Manage boss bars

Each bar is identified by a caller-chosen string. Bars are created lazily the first time an
identifier is used:

```java
import java.util.List;
import org.bukkit.entity.Player;

public void showServerBar(Player player) {
    // Configure the bar before showing it so viewers never see the defaults.
    bossBar.updateTitle("server-info", "&6play.example.com");
    bossBar.updateProgress("server-info", 0.5f);

    bossBar.showFor("server-info", player);
}

public void showForEveryone(List<Player> players) {
    bossBar.showFor("server-info", players);
}

public void hide(Player player) {
    // The bar is retained and can be shown again later.
    bossBar.hideFor("server-info", player);
}

public void hideEverything() {
    // Hides every bar and discards all identifiers.
    bossBar.clearAll();
}
```

Notes:

- Reusing the same identifier shows the same bar to every viewer, so an update is immediately
  visible to all of them.
- A new identifier starts with an empty title, full progress, and the implementation's default
  color/style. Update the title and progress before showing it.
- `hideFor` only removes viewers; the bar and its values are kept. Use `clearAll` to discard them.
- Call the API from the server thread. The library does not add its own scheduling.

## API overview

```java
public interface IBossBar {

    void hideAll();

    void clearAll();

    void showFor(String id, Player player);

    void showFor(String id, List<Player> players);

    void hideFor(String id, Player player);

    void hideFor(String id, List<Player> players);

    void updateProgress(String id, float progress);

    void updateTitle(String id, String title);
}
```

| Method | Description |
| --- | --- |
| `hideAll()` | Hides every managed bar from its current viewers but keeps the bars. |
| `clearAll()` | Hides every managed bar and discards all bars. |
| `showFor(String, Player)` | Shows a bar to one player. |
| `showFor(String, List)` | Shows a bar to a list of players. |
| `hideFor(String, Player)` | Hides a bar from one player. |
| `hideFor(String, List)` | Hides a bar from a list of players. |
| `updateProgress(String, float)` | Sets the progress from `0.0` to `1.0`. |
| `updateTitle(String, String)` | Sets the title, using legacy color codes or MiniMessage. |

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

## Contact

For issues or feature requests, use the
[GitHub issue tracker](https://github.com/TavstalDev/yggraBossBar/issues).
