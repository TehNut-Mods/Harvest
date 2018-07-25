# Harvest [![](http://cf.way2muchnoise.eu/full_simpleharvest_downloads.svg)![](http://cf.way2muchnoise.eu/versions/simpleharvest.svg)](https://minecraft.curseforge.com/projects/simpleharvest)

Adds right click crop harvesting that is configurable via a JSON file.

**Note: The current codebase for 1.13 requires the use of an item since the necessary interaction hooks do not exist in Rift.**

Also the textures are temporary. I'm just not an artist.

## Default Config
    {
      "crops": [
        {
          "initialBlock": "minecraft:wheat[age=7]",
          "finalBlock": "minecraft:wheat[age=0]"
        },
        {
          "initialBlock": "minecraft:carrots[age=7]",
          "finalBlock": "minecraft:carrots[age=0]"
        },
        {
          "initialBlock": "minecraft:potatoes[age=7]",
          "finalBlock": "minecraft:potatoes[age=0]"
        },
        {
          "initialBlock": "minecraft:beetroots[age=3]",
          "finalBlock": "minecraft:beetroots[age=0]"
        },
        {
          "initialBlock": "minecraft:nether_wart[age=3]",
          "finalBlock": "minecraft:nether_wart[age=0]"
        }
      ],
      "exhaustionPerHarvest": 0.005,
      "additionalLogging": false,
      "checkForCrops": false
    }
