# Waystone Wings

## Status

Planning only. This document defines the intended design before any mod code is written.

## Mod naming

The chosen working name is **Waystone Wings**. It directly communicates the project's two central ideas: the Elytra's wings and its new role in creating Waystones.

The mod id, package name, repository name, and translation namespace should be based on a lowercase, uniqueness-checked form of the name when implementation begins. The public display name should remain “Waystone Wings.”

## Project summary

Create a NeoForge mod for Minecraft 1.21.1 that changes the Elytra from a flight item into a progression ingredient for the Waystones mod.

The Elytra must remain obtainable from End Ships, but it must no longer enable gliding or flight. Its tooltip will explain the restriction and describe its new purpose. Waystones and the other Waystones-related items will be crafted through Create machinery, with the Elytra serving as the central progression component.

## Goals

- Target NeoForge and Minecraft 1.21.1.
- Use mixins where vanilla or another mod's behavior cannot be changed cleanly through a supported event or extension point.
- Prevent Elytra flight in every dimension and game context covered by the final implementation.
- Prevent the Elytra from being equipped in the player's chest/back equipment slot.
- Keep End Ship Elytra generation and loot unchanged unless testing identifies an incompatibility.
- Add a clear Elytra tooltip explaining that it cannot be used for flight and what it is now used for.
- Rename the displayed item to **Elytra Warp Core**.
- Give the Elytra a custom texture that visually matches its new teleportation/energy purpose.
- Add configuration options for the major behavior, messaging, presentation, and recipe-overhaul decisions.
- Replace the relevant Waystones crafting recipes with Create processing recipes.
- Make the Elytra the defining ingredient in the Waystones progression chain.
- Rework recipes for the broader Waystones item set, not only the primary Waystone block.
- Keep the recipe chain understandable, automatable, and balanced for a modded survival world.

## Non-goals for the first implementation

- No changes to End Ship generation or Elytra drop rates by default.
- No custom flight system or alternative player movement system.
- No unnecessary changes to unrelated Create or Waystones content.
- No assumption that every Waystones item must use the exact same processing sequence; recipes should fit each item's role and value.

## Core gameplay design

### Elytra behavior

The Elytra remains the normal vanilla item and remains discoverable in End Ships. When equipped, it should not allow the player to enter or maintain Elytra gliding. Firework boosting should therefore not provide a way around the restriction.

The implementation should decide whether to:

1. prevent the player from starting a glide;
2. immediately cancel an already-active glide; or
3. do both, so the behavior is robust against state changes, commands, packets, and other mods.

The expected player-facing result is simple: equipping the Elytra does not create a usable flight item anywhere.

### Elytra equipment

The Elytra should not be equipable in the vanilla chest armor slot, commonly understood by players as the back slot. This prevents the item from occupying an armor slot when it has no wearable flight function, while still allowing it to remain a normal inventory item and recipe ingredient.

The implementation should define safe behavior for an Elytra that is already equipped when the mod is added or updated. The preferred behavior is to prevent future equipping without silently deleting the item; any forced unequip or migration behavior must preserve the item and be tested for both single-player and server play.

### Elytra tooltip

The tooltip should communicate two things without requiring a wiki:

- flight/gliding is disabled;
- the Elytra is now used as a component in the creation of Waystones-related items.

The text should be translatable, concise, and visually distinguishable from the vanilla durability text. Exact wording is still open; a working draft is:

> Cannot be used for flight.
>
> Used to create Waystones technology.

The tooltip should be added without removing useful vanilla information such as durability.

### Elytra Warp Core identity

The vanilla Elytra item should remain the underlying item so End Ship loot, existing inventories, and recipe input compatibility are preserved. Its displayed name will be changed from “Elytra” to **“Elytra Warp Core”** through the mod's language resources, and its item texture/model resources will be replaced with a custom design that suggests a dimensional energy core rather than wearable wings.

The texture should remain recognizable enough that players understand it is the former Elytra, while clearly signaling that it is no longer equipment. The final art direction is still open, but should favor a compact magical/industrial core, dimensional glow, and visual compatibility with Create and Waystones.

## Recipe direction

The recipe system should use Create's sequenced assembly where it improves the progression fantasy and communicates the processing steps. The exact machines and ingredients should be finalized after the installed Create and Waystones versions are selected.

### Primary Waystone concept

The proposed baseline chain is:

1. Start with a distinct base block or item appropriate to the Waystones theme.
2. The first Deployer applies the Elytra.
3. Additional Deployers apply the required Waystones ingredients in a deliberate order.
4. Finish with an appropriate Create operation, such as mixing, compacting, pressing, or another machine step.
5. Produce the completed Waystone item, with a transitional incomplete item used by sequenced assembly as needed.

The Elytra should be consumed or irreversibly incorporated by the recipe. The design should avoid creating a practical way to recover an intact Elytra from a finished Waystone unless that is explicitly desired for balance.

### Other Waystones items

Inventory and recipe coverage should include every item provided by the selected Waystones version, for example the primary Waystone, Sharestone, Warp Stone, Return Scroll, Warp Scroll, Bound Scroll, and Warp Plate if present. The final item list must be verified against the actual dependency rather than relying on these examples.

Each item should receive a recipe treatment based on its function:

- permanent world infrastructure should use the heavier Elytra-centered progression;
- reusable transport tools may use a related assembly or finishing process;
- consumable scrolls may use a lighter recipe that still depends on the new progression materials;
- duplicate or alternative Waystones blocks should not accidentally remain available through their original cheap recipes.

The plan should explicitly decide whether the Elytra is required once per crafted item, once per infrastructure tier, or only for an intermediate component shared by multiple recipes.

## Technical approach

### Project setup

- Establish a NeoForge 1.21.1 mod project with a stable mod id, package name, display name, and license.
- Pin compatible versions of Create and Waystones.
- Confirm whether Create and Waystones both run on the selected NeoForge/Minecraft versions.
- Add the mixin configuration and refmap setup required by the chosen NeoForge toolchain.
- Keep dependency-specific code isolated so updates are easier to manage.

### Elytra restriction

Investigate supported NeoForge hooks first. If they do not cover all required vanilla Elytra state transitions, use a narrowly scoped mixin against the player/entity Elytra-flight logic.

The mixin design should:

- be server-authoritative where possible;
- avoid affecting unrelated armor or movement;
- cancel both normal gliding and firework-assisted Elytra flight;
- prevent equipping the Elytra in the chest/back equipment slot;
- preserve Elytra items when rejecting an equip action;
- behave consistently in single-player and dedicated-server environments;
- fail safely if an expected target method changes in a future mapping or dependency update.

The reference project is useful as behavioral precedent, but its dimension-based configuration is not the desired default here: this project intends to disable Elytra flight universally.

### Tooltip

Use the appropriate NeoForge item tooltip event or item integration point for the selected mappings. Keep the text in language files and add a dedicated translation key. The tooltip should work in inventory screens, JEI/REI-style viewers if they display normal item tooltips, and when the item is held.

### Recipe registration

Register Create-compatible processing and sequenced-assembly recipes using data-driven recipe JSON where possible. Use code registration only where the format or runtime behavior requires it.

Recipe identifiers should be namespaced under this mod, and original Waystones recipes should be replaced or disabled deterministically. Recipe conflicts and duplicate outputs must be tested with both mods loaded.

### Compatibility and configuration

The mod should expose configuration options for server owners and pack makers rather than hard-coding every gameplay decision. Gameplay-affecting options should be server-side or common configuration so clients cannot bypass them.

Proposed configuration groups:

#### Elytra behavior

- `disableFlight`: disable Elytra gliding; default `true`.
- `disableFireworkBoosting`: prevent firework boosting through Elytra flight; default `true`.
- `disableEquipping`: prevent equipping the Elytra in the chest/back slot; default `true`.
- `handleAlreadyEquipped`: define how an already-equipped Elytra is handled after configuration changes or an update; default should preserve the item and safely prevent continued use.
- `showFlightWarning`: show an action-bar or chat warning when a player attempts restricted flight; default should be decided after playtesting.
- `flightWarningMessage`: configurable warning text.

#### Elytra presentation

- `renameElytra`: use “Elytra Warp Core” as the displayed name; default `true`.
- `showPurposeTooltip`: add the restriction and Waystones-purpose tooltip; default `true`.
- `useWarpCoreTexture`: enable the custom texture/model presentation; default `true`.

Presentation options may be client-side, but the item name and tooltip should use translation keys so resource packs and translations can override them cleanly.

#### Recipe overhaul

- `replaceWaystonesRecipes`: enable the Create-based Waystones recipe replacements; default `true`.
- `requireElytraWarpCore`: require the Elytra Warp Core progression component in the affected recipes; default `true`.
- `replaceWaystonesItemRecipes`: apply the overhaul to the broader Waystones item set, not only the primary Waystone; default `true`.
- `allowOriginalRecipes`: emergency compatibility option for pack development; default `false`.

The final configuration names and types should be reviewed against NeoForge's supported common/server/client configuration model before implementation. Options should be kept minimal if two settings would create confusing or contradictory states.

Potential future options include:

- allowing administrators to disable the recipe overhaul;
- choosing whether the tooltip describes the restriction only or also the new purpose;
- allowing a server to select between universal restriction and dimension-based restriction.

The dimension-based option is not part of the current default design: universal flight and equip restrictions are the intended behavior unless a server owner configures otherwise.

## Suggested milestones

### Milestone 1: dependency and version audit

- Select exact NeoForge, Create, and Waystones versions.
- Record their artifact ids, required dependencies, and license constraints.
- Inspect the actual Waystones item registry and original recipe set.
- Confirm Create sequenced-assembly support on the selected versions.

### Milestone 2: gameplay specification

- Finalize the Elytra restriction semantics.
- Finalize the “Elytra Warp Core” name, texture direction, tooltip wording, and translations.
- Finalize which settings are server/common versus client-only.
- Create a complete Waystones item/recipe matrix.
- Decide the role of the base block, intermediate item, Elytra consumption, and final Create operation.
- Define target costs and expected progression stage.

### Milestone 3: minimal technical prototype

- Create the empty NeoForge project.
- Validate mixin loading in a development client and dedicated server.
- Prototype the Elytra restriction independently of recipe work.
- Verify that End Ship Elytra loot remains available.
- Verify the renamed item and custom texture load without changing the underlying item identity.

### Milestone 4: recipe overhaul

- Implement the primary Waystone sequenced assembly.
- Implement recipes for the remaining Waystones items in groups.
- Remove or override the original recipes.
- Add JEI/REI-visible process guidance if supported by the selected Create integration.

### Milestone 5: testing and balance

- Test gliding, firework use, Elytra durability, death/relog, dimension changes, and server/client synchronization.
- Test equipping from inventory, hotbar, dispenser, commands, and any supported equipment-slot integration.
- Test that rejected equip attempts do not delete or duplicate the Elytra.
- Test all recipe stages, invalid inputs, byproducts, repeated assembly, and automation.
- Test End Ship acquisition in a fresh world.
- Test with only the required dependencies and with a representative modded environment.
- Review recipe costs so the system is meaningful without making Waystones impractical.

### Milestone 6: release readiness

- Add README, changelog, license, and supported-version documentation.
- Document required dependencies and recipe progression.
- Build the mod and test the distributable artifact on a clean instance.
- Record known incompatibilities and mixin target assumptions.

## Recipe planning matrix

This table is intentionally a design worksheet, not a final recipe specification.

| Item or tier | Proposed input | Elytra relationship | Create process | Design questions |
|---|---|---|---|---|
| Primary Waystone | New themed base block/item | First Deployer applies Elytra | Sequenced assembly plus final Create step | What is the base block and final operation? |
| Sharestone or equivalent | Related infrastructure base | Directly or through shared intermediate | Assembly or compacting | Should it be cheaper than a Waystone? |
| Warp Plate or equivalent | Plate/block base | Directly or through shared intermediate | Pressing, assembly, or both | Is it infrastructure or a transport tool tier? |
| Warp Stone | Tool base | Likely shared advanced component | Assembly or crafting plus finishing | How should durability/renewability affect cost? |
| Return/Warp Scroll | Paper or scroll base | Prefer shared progression material if not one Elytra each | Mixing, filling, or lighter assembly | Should consumables require the full Elytra cost? |
| Bound Scroll | Scroll base plus binding component | Derived from Waystones progression | Assembly or final Create step | What binds it to a location? |

## Open decisions

- What lowercase mod id and package namespace should represent Waystone Wings?
- Which exact Create and Waystones releases should be supported?
- Should the restriction apply to creative-mode players, commands, and other mods that directly set fall-flying state?
- Should the player receive an action-bar/chat warning when attempting to glide, or is the tooltip sufficient?
- Should “Elytra Warp Core” use a subtitle or additional tooltip line to explain its role as a dimensional anchor or teleportation catalyst?
- Which base block best fits the progression: an existing vanilla block, a Create component, or a new mod item?
- Should all infrastructure items require an Elytra-derived intermediate, allowing one Elytra to support multiple recipes, or should each major item require its own Elytra?
- Which ingredients should be applied by each Deployer, and in what order?
- Which finishing process best communicates the final transformation: mixing, compacting, pressing, or another Create machine?
- Should recipe outputs preserve or consume NBT, location data, or other Waystones state?
- Are there Waystones recipes that must remain unchanged for compatibility or early-game access?
- Should the recipe overhaul be configurable by server owners?

## Acceptance criteria

The project is ready for implementation when:

- exact dependency versions are recorded;
- the full Waystones item list for that version is documented;
- every affected original recipe has a replacement or an explicit reason to remain;
- the primary Waystone assembly sequence is specified step-by-step;
- Elytra restriction behavior is defined for normal play, firework use, relogging, and dimension changes;
- Elytra equipment behavior is defined for normal equip attempts and already-equipped items;
- “Elytra Warp Core,” its tooltip wording, texture direction, and translation keys are approved;
- configuration ownership and defaults are documented for each setting;
- the desired balance target and expected progression stage are written down.

## Reference

- [Disable Elytra Outside The End](https://github.com/Timtaran/disable-elytra-outside-the-end) — behavioral reference for preventing Elytra flight and communicating a restriction to players.
