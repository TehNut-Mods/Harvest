# Harvest [![](http://cf.way2muchnoise.eu/full_simpleharvest_downloads.svg)![](http://cf.way2muchnoise.eu/versions/simpleharvest.svg)](https://minecraft.curseforge.com/projects/simpleharvest)

Adds right click crop harvesting that is configurable via a JSON file.

## Default Config

```json
{
  "exhaustionPerHarvest": 0.005,
  "additionalLogging": true,
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

### Additional Configuration

In order for Harvest to support a crop, it must be able to find a seed in it's drop list. To specify new types of seeds, use a data pack to define new seed items in the [`harvest:seeds` item tag](https://github.com/TehNut/Harvest/blob/1.14_forge/src/main/resources/data/harvest/tags/items/seeds.json).

## Developers

If your crop requires special harvest handling, you can register a custom [`IReplantHandler`](https://github.com/TehNut/Harvest/blob/1.14_forge/src/main/java/tehnut/harvest/IReplantHandler.java) for it through [`Harvest.CUSTOM_HANDLERS`](https://github.com/TehNut/Harvest/blob/1.14_forge/src/main/java/tehnut/harvest/Harvest.java#L40).