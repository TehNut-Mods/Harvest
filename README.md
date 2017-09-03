# Harvest [![](http://cf.way2muchnoise.eu/full_simpleharvest_downloads.svg)![](http://cf.way2muchnoise.eu/versions/simpleharvest.svg)](https://minecraft.curseforge.com/projects/simpleharvest)

Adds right click crop harvesting that is configurable via a JSON file.

## Default Config
    {
      "crops": [
        {
          "initialBlock": {
            "blockName": "minecraft:wheat",
            "meta": 7
          }
        },
        {
          "initialBlock": {
            "blockName": "minecraft:carrots",
            "meta": 7
          }
        },
        {
          "initialBlock": {
            "blockName": "minecraft:potatoes",
            "meta": 7
          }
        },
        {
          "initialBlock": {
            "blockName": "minecraft:beetroots",
            "meta": 3
          }
        },
        {
          "initialBlock": {
            "blockName": "minecraft:nether_wart",
            "meta": 3
          }
        }
      ],
      "exhaustionPerHarvest": 0.005,
      "additionalLogging": false,
      "checkForCrops": false
    }