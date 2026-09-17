# Recipe matrix

Waystone Wings targets Waystones 21.1.45 and replaces every recipe shipped by that version. The generated JSON is committed under `src/main/resources`; rerun `scripts/generate_recipes.ps1` after changing the matrix.

| Family | Elytra relationship | Create process |
| --- | --- | --- |
| Calibrated Warp Core | Consumes one vanilla Elytra Warp Core | Sequenced assembly: Precision Mechanism, Eye of Ender, Brass Sheet, pressing |
| Waystone | Consumes one Calibrated Warp Core | Sequenced assembly on Stone Bricks with Obsidian and Amethyst |
| Sharestones | Converts one Waystone, preserving its one-core cost | Deployer applies the selected dye |
| Portstones | Converts one Waystone, preserving its one-core cost | Deployer applies the selected dye |
| Waystone material variants | Converts one Waystone, preserving its one-core cost | Deployer applies the matching building material |
| Warp Plate | Consumes one Calibrated Warp Core | Sequenced assembly with Dormant Shard and Sturdy Sheet |
| Warp Stone | Consumes one Calibrated Warp Core | Sequenced assembly with Amethyst and Emerald |
| Warp Dust and shards | Downstream material; no Elytra per consumable | Mixing, compacting, or pressing |
| Scrolls | Downstream material; no Elytra per scroll | Compacting from Blank Scroll and warp reagents |
| Twinbound Feather and Epitaph | Downstream utility items | Deploying or compacting |

`replaceWaystonesRecipes`, `replaceWaystonesItemRecipes`, `requireElytraWarpCore`, and `allowOriginalRecipes` are evaluated as NeoForge data-load conditions. Change them before a restart or `/reload`.
