# Blockstates

## Defaut

```json
{
  "variants": {
    "normal": {
      "model": "updatemod:block"
    }
  }
}
```
## bed
```json
{
    "variants": {
        "facing=north,part=foot": { "model": "updatemod:bed_foot", "y": 180 },
        "facing=east,part=foot":  { "model": "updatemod:bed_foot", "y": 270 },
        "facing=south,part=foot": { "model": "updatemod:bed_foot" },
        "facing=west,part=foot":  { "model": "updatemod:bed_foot", "y": 90 },
        "facing=north,part=head": { "model": "updatemod:bed_head", "y": 180 },
        "facing=east,part=head":  { "model": "updatemod:bed_head", "y": 270 },
        "facing=south,part=head": { "model": "updatemod:bed_head" },
        "facing=west,part=head":  { "model": "updatemod:bed_head", "y": 90 }
    }
}
```
## log
```json
{
  "variants": {
    "axis=y":  { "model": "updatemod:acacia_log" },
    "axis=z":   { "model": "updatemod:acacia_log", "x": 90 },
    "axis=x":   { "model": "updatemod:acacia_log", "x": 90, "y": 90 },
    "axis=none": { "model": "updatemod:acacia_bark" }
  }
}
```
## slab
```json
{
  "variants": {
    "type=top": { "model": "updatemod:acacia_slab_top" },
    "type=bottom": { "model": "updatemod:acacia_slab_bottom" }
  }
}
```
## stairs
```json
{
  "variants": {
    "facing=east,half=bottom,shape=straight":  { "model": "birch_stairs" },
    "facing=west,half=bottom,shape=straight":  { "model": "birch_stairs", "y": 180, "uvlock": true },
    "facing=south,half=bottom,shape=straight": { "model": "birch_stairs", "y": 90, "uvlock": true },
    "facing=north,half=bottom,shape=straight": { "model": "birch_stairs", "y": 270, "uvlock": true },
    "facing=east,half=bottom,shape=outer_right":  { "model": "birch_outer_stairs" },
    "facing=west,half=bottom,shape=outer_right":  { "model": "birch_outer_stairs", "y": 180, "uvlock": true },
    "facing=south,half=bottom,shape=outer_right": { "model": "birch_outer_stairs", "y": 90, "uvlock": true },
    "facing=north,half=bottom,shape=outer_right": { "model": "birch_outer_stairs", "y": 270, "uvlock": true },
    "facing=east,half=bottom,shape=outer_left":  { "model": "birch_outer_stairs", "y": 270, "uvlock": true },
    "facing=west,half=bottom,shape=outer_left":  { "model": "birch_outer_stairs", "y": 90, "uvlock": true },
    "facing=south,half=bottom,shape=outer_left": { "model": "birch_outer_stairs" },
    "facing=north,half=bottom,shape=outer_left": { "model": "birch_outer_stairs", "y": 180, "uvlock": true },
    "facing=east,half=bottom,shape=inner_right":  { "model": "birch_inner_stairs" },
    "facing=west,half=bottom,shape=inner_right":  { "model": "birch_inner_stairs", "y": 180, "uvlock": true },
    "facing=south,half=bottom,shape=inner_right": { "model": "birch_inner_stairs", "y": 90, "uvlock": true },
    "facing=north,half=bottom,shape=inner_right": { "model": "birch_inner_stairs", "y": 270, "uvlock": true },
    "facing=east,half=bottom,shape=inner_left":  { "model": "birch_inner_stairs", "y": 270, "uvlock": true },
    "facing=west,half=bottom,shape=inner_left":  { "model": "birch_inner_stairs", "y": 90, "uvlock": true },
    "facing=south,half=bottom,shape=inner_left": { "model": "birch_inner_stairs" },
    "facing=north,half=bottom,shape=inner_left": { "model": "birch_inner_stairs", "y": 180, "uvlock": true },
    "facing=east,half=top,shape=straight":  { "model": "birch_stairs", "x": 180, "uvlock": true },
    "facing=west,half=top,shape=straight":  { "model": "birch_stairs", "x": 180, "y": 180, "uvlock": true },
    "facing=south,half=top,shape=straight": { "model": "birch_stairs", "x": 180, "y": 90, "uvlock": true },
    "facing=north,half=top,shape=straight": { "model": "birch_stairs", "x": 180, "y": 270, "uvlock": true },
    "facing=east,half=top,shape=outer_right":  { "model": "birch_outer_stairs", "x": 180, "y": 90, "uvlock": true },
    "facing=west,half=top,shape=outer_right":  { "model": "birch_outer_stairs", "x": 180, "y": 270, "uvlock": true },
    "facing=south,half=top,shape=outer_right": { "model": "birch_outer_stairs", "x": 180, "y": 180, "uvlock": true },
    "facing=north,half=top,shape=outer_right": { "model": "birch_outer_stairs", "x": 180, "uvlock": true },
    "facing=east,half=top,shape=outer_left":  { "model": "birch_outer_stairs", "x": 180, "uvlock": true },
    "facing=west,half=top,shape=outer_left":  { "model": "birch_outer_stairs", "x": 180, "y": 180, "uvlock": true },
    "facing=south,half=top,shape=outer_left": { "model": "birch_outer_stairs", "x": 180, "y": 90, "uvlock": true },
    "facing=north,half=top,shape=outer_left": { "model": "birch_outer_stairs", "x": 180, "y": 270, "uvlock": true },
    "facing=east,half=top,shape=inner_right":  { "model": "birch_inner_stairs", "x": 180, "y": 90, "uvlock": true },
    "facing=west,half=top,shape=inner_right":  { "model": "birch_inner_stairs", "x": 180, "y": 270, "uvlock": true },
    "facing=south,half=top,shape=inner_right": { "model": "birch_inner_stairs", "x": 180, "y": 180, "uvlock": true },
    "facing=north,half=top,shape=inner_right": { "model": "birch_inner_stairs", "x": 180, "uvlock": true },
    "facing=east,half=top,shape=inner_left":  { "model": "birch_inner_stairs", "x": 180, "uvlock": true },
    "facing=west,half=top,shape=inner_left":  { "model": "birch_inner_stairs", "x": 180, "y": 180, "uvlock": true },
    "facing=south,half=top,shape=inner_left": { "model": "birch_inner_stairs", "x": 180, "y": 90, "uvlock": true },
    "facing=north,half=top,shape=inner_left": { "model": "birch_inner_stairs", "x": 180, "y": 270, "uvlock": true }
  }
}

```
## rail

```json
{
  "variants": {
    "powered=false,shape=north_south":  { "model": "detector_rail_flat" },
    "powered=false,shape=east_west":  { "model": "detector_rail_flat", "y": 90 },
    "powered=false,shape=ascending_east": { "model": "detector_rail_raised_ne", "y": 90 },
    "powered=false,shape=ascending_west": { "model": "detector_rail_raised_sw", "y": 90 },
    "powered=false,shape=ascending_north": { "model": "detector_rail_raised_ne" },
    "powered=false,shape=ascending_south": { "model": "detector_rail_raised_sw" },
    "powered=true,shape=north_south":  { "model": "detector_rail_powered_flat" },
    "powered=true,shape=east_west":  { "model": "detector_rail_powered_flat", "y": 90 },
    "powered=true,shape=ascending_east": { "model": "detector_rail_powered_raised_ne", "y": 90 },
    "powered=true,shape=ascending_west": { "model": "detector_rail_powered_raised_sw", "y": 90 },
    "powered=true,shape=ascending_north": { "model": "detector_rail_powered_raised_ne" },
    "powered=true,shape=ascending_south": { "model": "detector_rail_powered_raised_sw" }
  }
}
```
## dispenser
```json
{
  "variants": {
    "facing=down":  { "model": "dispenser_vertical", "x": 180 },
    "facing=up":    { "model": "dispenser_vertical" },
    "facing=north": { "model": "dispenser" },
    "facing=south": { "model": "dispenser", "y": 180 },
    "facing=west":  { "model": "dispenser", "y": 270 },
    "facing=east":  { "model": "dispenser", "y": 90 }
  }
}
```