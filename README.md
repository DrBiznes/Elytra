# Waystone Wings

I wanted to disable elytra flight on my aeronautics smp and made the elytra a progression item in the waystones mod. There are also optional Create mod specific recipes for Waystones that are enabled if it's installed.

## Elytra recipe

Calibrated Warp Core is the item that actually consumes the Elytra, and the gate for every other Waystones recipe below it. Vanilla crafting-table recipe (default, or whenever Create's sequenced-assembly version below isn't used):

| Item | Ingredients |
| --- | --- |
| Calibrated Warp Core | Elytra (or Nether Star, config toggle) + 4 Gold Ingot + 2 Copper Ingot + Ender Eye + Comparator |

## Create recipes

Everything crafts at a vanilla table by default. With Create installed, these families switch to a machine-based sequenced-assembly recipe instead (configurable per family, or globally, in the mod config):

| Item | Create process |
| --- | --- |
| Calibrated Warp Core | Elytra (or Nether Star) → Precision Mechanism → Ender Eye → Brass Plate → press |
| Waystone | Stone Bricks → Calibrated Warp Core → Obsidian → Amethyst Shard → press |
| Waystone material variants | Building material → Calibrated Warp Core → Obsidian → Amethyst Shard → press |
| Portstones | Waystone → Dye deploy |
| Sharestones | Portstone → Obsidian deploy |
| Warp Plate | Polished Andesite → Warp Stone → Dormant Shard → Warp Dust → Sturdy Sheet → press |
| Blank Scroll | Paper → Feather → Gold Nugget → compact |
| Warp Dust | Ender Pearl + Amethyst Shard → crushing wheels |
| Deepslate Shard | Deepslate → mechanical press |

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.248 or newer 21.1.x
- Waystones 21.1.45
- Balm 21.0.65
- Create 6.0.10 (optional)
- Java 21

## Configuration

Gameplay and recipe settings are written to `config/waystone_wings-common.toml`. Name, tooltip, and inventory-model settings are written to `config/waystone_wings-client.toml`. Recipe options require a world/server restart; the inventory model option requires a client restart. On a dedicated server, gameplay and recipe settings must be changed in the server's own common config.

## License

Waystone Wings source code is MIT licensed.

## Attribution

- [Waystones](https://modrinth.com/mod/waystones) by BlayTheNinth
- [Create](https://modrinth.com/mod/create) by simibubi and the Create team
