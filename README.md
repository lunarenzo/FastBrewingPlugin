# FastBrewing

A lightweight, ultra-high-performance Paper, Spigot, and Folia plugin designed to customize brewing stand speeds on Minecraft servers.

## Features
- **Ultra-Low MSPT Overhead:** Zero tick-loop polling; operates on 100% event-driven native handlers (`BrewingStartEvent`).
- **Folia Multi-Threading Support:** 100% compatible with Folia region threading without thread safety issues or crashes.
- **Configurable Speed:** Custom brewing speeds from instant (1 tick) to custom tick rates.
- **Per-World Filtering:** Restrict fast brewing to specific worlds.
- **Hot-Reloadable:** Reload settings instantly without restarting the server via `/fastbrewing reload`.

---

## Installation
1. Download `FastBrewing-1.0.0.jar` from the GitHub Actions build artifacts or Releases.
2. Drop the `.jar` into your server's `plugins/` directory.
3. Restart your server.

---

## Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/fastbrewing reload` | `fastbrewing.admin` | Reloads `config.yml` settings instantly. |

---

## Configuration Guide (`plugins/FastBrewing/config.yml`)

```yaml
# FastBrewing Configuration

# Global toggle for the plugin feature
enabled: true

# Instant brewing mode (1 tick / 0.05s). Overrides brew-time-ticks when true.
instant-brewing: false

# Custom brewing duration in ticks (20 ticks = 1 second). Vanilla is 400 ticks (20 seconds).
brew-time-ticks: 20

# List of worlds where fast brewing is enabled. Use "*" for all worlds.
allowed-worlds:
  - "*"
```

### Config Key Explanations

* **`enabled`** (`boolean`): Toggles fast brewing on (`true`) or off (`false`). Default: `true`.
* **`instant-brewing`** (`boolean`): If set to `true`, potions finish brewing instantly in 1 tick (0.05 seconds). Default: `false`.
* **`brew-time-ticks`** (`integer`): Defines the custom brewing duration in Minecraft ticks. For example, `20` sets brewing speed to 1 second, `100` sets it to 5 seconds. Default: `20`.
* **`allowed-worlds`** (`list of strings`): Specifies which worlds permit fast brewing. Example: `["world", "world_nether"]` or `["*"]` for global activation.

---

## Authors & License
* **Author:** lunarenzo
* **Group:** lunatech
