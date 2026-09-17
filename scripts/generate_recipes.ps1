Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$resourceRoot = Join-Path $projectRoot 'src/main/resources'
$waystonesOverrideRoot = Join-Path $resourceRoot 'data/waystones/recipe'
$recipeRoot = Join-Path $resourceRoot 'data/waystone_wings/recipe'

New-Item -ItemType Directory -Force -Path $waystonesOverrideRoot, $recipeRoot | Out-Null

function Write-JsonFile([string] $Path, [object] $Value) {
    $directory = Split-Path -Parent $Path
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
    $json = $Value | ConvertTo-Json -Depth 30
    [System.IO.File]::WriteAllText($Path, $json + [Environment]::NewLine, [System.Text.UTF8Encoding]::new($false))
}

function Item([string] $Id) { return [ordered]@{ item = $Id } }
function Tag([string] $Id) { return [ordered]@{ tag = $Id } }
function Stack([string] $Id, [int] $Count = 1) {
    if ($Count -eq 1) { return [ordered]@{ id = $Id } }
    return [ordered]@{ id = $Id; count = $Count }
}
function Condition([string] $Group = 'core', [bool] $Original = $false, [Nullable[bool]] $ElytraRequired = $null) {
    $condition = [ordered]@{ type = 'waystone_wings:recipe_config'; group = $Group; original = $Original }
    if ($null -ne $ElytraRequired) { $condition.elytra_required = [bool] $ElytraRequired }
    return $condition
}
function With-Condition([object] $Recipe, [string] $Group = 'core', [bool] $Original = $false, [Nullable[bool]] $ElytraRequired = $null) {
    $result = [ordered]@{ 'neoforge:conditions' = @((Condition $Group $Original $ElytraRequired)) }
    foreach ($entry in $Recipe.GetEnumerator()) { $result[$entry.Key] = $entry.Value }
    return $result
}
function Shaped([string[]] $Pattern, [hashtable] $Key, [string] $Output, [int] $Count = 1, [string] $Category = 'misc') {
    return [ordered]@{ type = 'minecraft:crafting_shaped'; category = $Category; key = $Key; pattern = $Pattern; result = (Stack $Output $Count) }
}
function Shapeless([object[]] $Ingredients, [string] $Output, [int] $Count = 1, [string] $Category = 'misc') {
    return [ordered]@{ type = 'minecraft:crafting_shapeless'; category = $Category; ingredients = $Ingredients; result = (Stack $Output $Count) }
}
function Processing([string] $Type, [object[]] $Ingredients, [string] $Output, [int] $Count = 1) {
    return [ordered]@{ type = "create:$Type"; ingredients = $Ingredients; results = @((Stack $Output $Count)) }
}
function Deploying([string] $BaseItem, [object] $Applied, [string] $Output, [int] $Count = 1) {
    # `$input` is an automatic PowerShell variable and parameter names are
    # case-insensitive, so using `$Input` here silently produced empty IDs.
    return Processing 'deploying' @((Item $BaseItem), $Applied) $Output $Count
}
function Sequenced([object] $Ingredient, [string] $Transitional, [object[]] $Applied, [string] $Output) {
    $sequence = @()
    foreach ($application in $Applied) {
        if ($application -is [string] -and $application -eq 'press') {
            $sequence += [ordered]@{
                type = 'create:pressing'
                ingredients = @((Item $Transitional))
                results = @((Stack $Transitional))
            }
        } else {
            $sequence += [ordered]@{
                type = 'create:deploying'
                ingredients = @((Item $Transitional), $application)
                results = @((Stack $Transitional))
            }
        }
    }
    return [ordered]@{
        type = 'create:sequenced_assembly'
        ingredient = $Ingredient
        transitional_item = (Stack $Transitional)
        sequence = $sequence
        results = @((Stack $Output))
        loops = 1
    }
}
function Write-Recipe([string] $RelativePath, [object] $Recipe, [string] $Group = 'core', [bool] $Original = $false, [Nullable[bool]] $ElytraRequired = $null) {
    Write-JsonFile (Join-Path $recipeRoot ($RelativePath + '.json')) (With-Condition $Recipe $Group $Original $ElytraRequired)
}

$colors = @('white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan', 'purple', 'blue', 'brown', 'green', 'red', 'black')
$shareColors = $colors | Where-Object { $_ -ne 'white' }
$originalNames = [System.Collections.Generic.List[string]]::new()

function Record-Original([string] $Name, [object] $Recipe, [string] $Group = 'core') {
    $originalNames.Add($Name)
    Write-JsonFile (Join-Path $waystonesOverrideRoot ($Name + '.json')) ([ordered]@{ 'neoforge:conditions' = @([ordered]@{ type = 'neoforge:false' }) })
    Write-Recipe "legacy/$Name" $Recipe $Group $true
}

# Recreate all original craftable Waystones 21.1.45 recipes behind the compatibility switch.
foreach ($color in $colors) {
    Record-Original "${color}_portstone" (Shaped @('DSD', 'SWS', 'BBB') @{
        D = (Tag "c:dyes/$color"); S = (Item 'minecraft:stone_bricks'); W = (Item 'waystones:warp_stone'); B = (Item 'minecraft:polished_andesite')
    } "waystones:${color}_portstone")
}
foreach ($color in $shareColors) {
    Record-Original "${color}_sharestone" (Shaped @('SSS', 'DWD', 'OOO') @{
        D = (Tag "c:dyes/$color"); S = (Item 'minecraft:stone_bricks'); W = (Item 'waystones:warp_stone'); O = (Item 'minecraft:obsidian')
    } "waystones:${color}_sharestone")
}

$waystoneMaterials = [ordered]@{
    waystone = 'minecraft:stone_bricks'
    mossy_waystone = 'minecraft:mossy_stone_bricks'
    sandy_waystone = 'minecraft:chiseled_sandstone'
    deepslate_waystone = 'minecraft:deepslate'
    blackstone_waystone = 'minecraft:blackstone'
    end_stone_waystone = 'minecraft:end_stone_bricks'
    red_nether_bricks_waystone = 'minecraft:red_nether_bricks'
    purpur_waystone = 'minecraft:purpur_block'
    prismarine_waystone = 'minecraft:prismarine'
    mud_bricks_waystone = 'minecraft:mud_bricks'
}
foreach ($entry in $waystoneMaterials.GetEnumerator()) {
    Record-Original $entry.Key (Shaped @(' S ', 'SWS', 'OOO') @{
        S = (Item $entry.Value); W = (Item 'waystones:warp_stone'); O = (Item 'minecraft:obsidian')
    } "waystones:$($entry.Key)")
}
Record-Original 'mossy_waystone_from_vines' (Shapeless @((Item 'waystones:waystone'), (Item 'minecraft:vine'), (Item 'minecraft:vine'), (Item 'minecraft:vine')) 'waystones:mossy_waystone')
Record-Original 'mossy_waystone_from_moss_blocks' (Shapeless @((Item 'waystones:waystone'), (Item 'minecraft:moss_block'), (Item 'minecraft:moss_block'), (Item 'minecraft:moss_block')) 'waystones:mossy_waystone')

Record-Original 'blank_scroll' (Shaped @('GFG', 'PPP') @{ G = (Tag 'c:nuggets/gold'); F = (Item 'minecraft:feather'); P = (Item 'minecraft:paper') } 'waystones:blank_scroll' 3) 'items'
Record-Original 'return_scroll' (Shaped @('GEG', 'PPP') @{ G = (Tag 'c:nuggets/gold'); E = (Item 'minecraft:ink_sac'); P = (Item 'minecraft:paper') } 'waystones:return_scroll' 3) 'items'
Record-Original 'warp_scroll' (Shaped @('GDG', 'GEG', 'PPP') @{ G = (Tag 'c:nuggets/gold'); D = (Item 'minecraft:ink_sac'); E = (Item 'minecraft:ender_pearl'); P = (Item 'minecraft:paper') } 'waystones:warp_scroll' 3) 'items'
Record-Original 'portal_scroll' (Shaped @('SIS', 'SES', 'PPP') @{ S = (Item 'minecraft:amethyst_shard'); I = (Item 'minecraft:ink_sac'); E = (Item 'minecraft:ender_pearl'); P = (Item 'minecraft:paper') } 'waystones:portal_scroll' 3) 'items'
Record-Original 'warp_stone' (Shaped @('DED', 'EGE', 'DED') @{ D = (Item 'minecraft:amethyst_shard'); E = (Item 'minecraft:ender_pearl'); G = (Tag 'c:gems/emerald') } 'waystones:warp_stone') 'items'
Record-Original 'warp_dust' (Shapeless @((Item 'minecraft:ender_pearl'), (Item 'minecraft:amethyst_shard')) 'waystones:warp_dust' 4) 'items'
Record-Original 'dormant_shard' (Shapeless @((Item 'waystones:warp_dust'), (Item 'waystones:warp_dust'), (Item 'minecraft:flint')) 'waystones:dormant_shard') 'items'
Record-Original 'deepslate_shard' (Shapeless @((Item 'minecraft:deepslate'), (Item 'minecraft:flint')) 'waystones:deepslate_shard') 'items'
Record-Original 'twinbound_feather' (Shapeless @((Item 'minecraft:feather'), (Item 'minecraft:amethyst_shard'), (Item 'minecraft:gold_nugget'), (Item 'minecraft:ink_sac')) 'waystones:twinbound_feather' 1 'equipment') 'items'
Record-Original 'epitaph' (Shaped @('GGG', 'ADA', 'GGG') @{ G = (Tag 'c:nuggets/gold'); A = (Item 'minecraft:amethyst_shard'); D = (Item 'minecraft:deepslate') } 'waystones:epitaph' 1 'equipment') 'items'
Record-Original 'warp_plate' (Shaped @('SWS', 'WFW', 'SWS') @{ S = (Item 'minecraft:stone_bricks'); W = (Item 'waystones:warp_dust'); F = (Item 'waystones:dormant_shard') } 'waystones:warp_plate')

# Create progression: calibrate an Elytra Warp Core, then consume one core per permanent/reusable device.
$coreSteps = @((Item 'create:precision_mechanism'), (Item 'minecraft:ender_eye'), (Tag 'c:plates/brass'), 'press')
Write-Recipe 'core/calibrated_warp_core_from_elytra' (Sequenced (Item 'minecraft:elytra') 'waystone_wings:incomplete_warp_core' $coreSteps 'waystone_wings:calibrated_warp_core') 'core' $false $true
Write-Recipe 'core/calibrated_warp_core_without_elytra' (Sequenced (Item 'minecraft:nether_star') 'waystone_wings:incomplete_warp_core' $coreSteps 'waystone_wings:calibrated_warp_core') 'core' $false $false

Write-Recipe 'infrastructure/waystone' (Sequenced (Item 'minecraft:stone_bricks') 'waystone_wings:incomplete_waystone' @(
    (Item 'waystone_wings:calibrated_warp_core'), (Item 'minecraft:obsidian'), (Item 'minecraft:amethyst_shard'), 'press'
) 'waystones:waystone')
Write-Recipe 'infrastructure/warp_plate' (Sequenced (Item 'minecraft:polished_andesite') 'waystone_wings:incomplete_warp_plate' @(
    (Item 'waystone_wings:calibrated_warp_core'), (Item 'waystones:dormant_shard'), (Item 'create:sturdy_sheet'), 'press'
) 'waystones:warp_plate')
Write-Recipe 'items/warp_stone' (Sequenced (Item 'minecraft:ender_eye') 'waystone_wings:incomplete_warp_stone' @(
    (Item 'waystone_wings:calibrated_warp_core'), (Item 'minecraft:amethyst_shard'), (Tag 'c:gems/emerald'), 'press'
) 'waystones:warp_stone') 'items'

foreach ($color in $colors) {
    Write-Recipe "infrastructure/portstones/$color" (Deploying 'waystones:waystone' (Tag "c:dyes/$color") "waystones:${color}_portstone")
}
foreach ($color in $shareColors) {
    Write-Recipe "infrastructure/sharestones/$color" (Deploying 'waystones:waystone' (Tag "c:dyes/$color") "waystones:${color}_sharestone")
}
foreach ($entry in $waystoneMaterials.GetEnumerator()) {
    if ($entry.Key -eq 'waystone') { continue }
    Write-Recipe "infrastructure/variants/$($entry.Key)" (Deploying 'waystones:waystone' (Item $entry.Value) "waystones:$($entry.Key)")
}

# Lighter consumables stay downstream of the overhauled materials without demanding one Elytra apiece.
Write-Recipe 'items/warp_dust' (Processing 'mixing' @((Item 'minecraft:ender_pearl'), (Item 'minecraft:amethyst_shard')) 'waystones:warp_dust' 4) 'items'
Write-Recipe 'items/dormant_shard' (Processing 'compacting' @((Item 'waystones:warp_dust'), (Item 'waystones:warp_dust'), (Item 'minecraft:flint')) 'waystones:dormant_shard') 'items'
Write-Recipe 'items/deepslate_shard' (Processing 'pressing' @((Item 'minecraft:deepslate')) 'waystones:deepslate_shard') 'items'
Write-Recipe 'items/blank_scroll' (Processing 'compacting' @((Item 'minecraft:paper'), (Item 'minecraft:paper'), (Item 'minecraft:paper'), (Item 'minecraft:feather'), (Tag 'c:nuggets/gold')) 'waystones:blank_scroll' 3) 'items'
Write-Recipe 'items/return_scroll' (Processing 'compacting' @((Item 'waystones:blank_scroll'), (Item 'minecraft:ink_sac'), (Tag 'c:nuggets/gold')) 'waystones:return_scroll') 'items'
Write-Recipe 'items/warp_scroll' (Processing 'compacting' @((Item 'waystones:blank_scroll'), (Item 'minecraft:ink_sac'), (Item 'waystones:warp_dust')) 'waystones:warp_scroll') 'items'
Write-Recipe 'items/portal_scroll' (Processing 'compacting' @((Item 'waystones:blank_scroll'), (Item 'minecraft:ink_sac'), (Item 'minecraft:ender_pearl'), (Item 'minecraft:amethyst_shard')) 'waystones:portal_scroll') 'items'
Write-Recipe 'items/twinbound_feather' (Deploying 'minecraft:feather' (Item 'waystones:warp_dust') 'waystones:twinbound_feather') 'items'
Write-Recipe 'items/epitaph' (Processing 'compacting' @((Item 'minecraft:deepslate'), (Item 'minecraft:amethyst_shard'), (Tag 'c:nuggets/gold'), (Tag 'c:nuggets/gold')) 'waystones:epitaph') 'items'

Write-Host "Generated $($originalNames.Count) original-recipe overrides and the Create replacement recipe set."
