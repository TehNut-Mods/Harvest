# Harvest [![](http://cf.way2muchnoise.eu/full_simpleharvest_downloads.svg)![](http://cf.way2muchnoise.eu/versions/simpleharvest.svg)](https://minecraft.curseforge.com/projects/simpleharvest)

Adds right click crop harvesting that is configurable via a JSON file.

This branch depends on [Fabric-Loader](https://fabricmc.net/) and [Fabric](https://minecraft.curseforge.com/projects/fabric).

## Default Config
```json
{
  "exhaustionPerHarvest": 0.005,
  "additionalLogging": false,
  "crops": [
    {
      "block": "minecraft:wheat",
      "states": {
        "age": "7"
      }
    },
    {
      "block": "minecraft:nether_wart",
      "states": {
        "age": "3"
      }
    },
    {
      "block": "minecraft:carrots",
      "states": {
        "age": "7"
      }
    },
    {
      "block": "minecraft:potatoes",
      "states": {
        "age": "7"
      }
    },
    {
      "block": "minecraft:beetroots",
      "states": {
        "age": "3"
      }
    }
  ]
}
```

## Compatibility

If your mod adds basic crops, please consider shipping a `harvest:seeds` tag defining your seed items. This will
allow users to define your crops in the config of this mod and have them function properly. 