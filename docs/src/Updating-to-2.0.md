Celestial 2.0 changes a few important things that will affect existing Celestial resource packs. This will go through how to update your Celestial resource pack to version 2.0.

## Base Degree Rotations
In this update, [base degree rotations](https://github.com/fishcute/Celestial/wiki/Rotation-Entry#base-degrees) **no longer default to -90, 0, -90**. It instead defaults to 0, 0, 0. Rotations for sky objects affected by this can be easily fixed by adding this to each sky object:
```
"rotation": {
	"base_degrees_x": "-90",
	"base_degrees_z": "-90"
}
```
Alternatively, if you wish for sky object base degrees to default to -90, 0, -90, a new option `legacy_rotations` can be enabled in your resource pack's `dimensions.json`
```
"legacy_rotations": true
```
It is encouraged to convert all sky objects using the first method, however, this second option will remain available in the future.

## Variables and Functions
In this update, variables and functions have received two changes. The first change is that the hashtag (#skyAngle) is no longer required (skyAngle). Existing hash tags will be ignored, so removing them is not necessary. It is still recommended to remove them, as they may be repurposed for other features in the future.

More importantly, multiple variables and functions have been renamed. This change was made to maintain consistency in Celestial variables, and another mass-renaming will likely never occur again.

Renaming every variable in a Celestial resource pack can take a while, but if you have [Notepad++](https://notepad-plus-plus.org/), you can easily rename variables and functions in a resource pack using its `Find in File` feature in the Find menu.

![find in file](https://github.com/user-attachments/assets/1860a6d0-ecb3-4a70-853e-1d74cf64d00e)

Below is a list of all renamed variables and functions:

### Variables
- `rainLevel` -> `rainAlpha`
- `thunderGradient` -> `thunderAlpha`
- `isRightClicking` -> `rightClicking`
- `isLeftClicking` -> `leftClicking`
- `isSubmerged` -> `submerged`
- `getGameTime` -> `gameTime`
- `getWorldTime` -> `worldTime`
- `getDayTime` -> `dayTime`
- `xPos` -> `posX`
- `yPos` -> `posY`
- `zPos` -> `posZ`

### Functions
- `isInBiome(...)` -> `inBiome(...)`
- `isUsing(...)` -> `rightClickingWith(...)`
- `isMiningWith(...)` -> `leftClickingWith(...)`
- `isHolding(...)` -> `holding(...)`
- `isInArea(...)` -> `inArea(...)`
- `distanceToBiomeIgnoreY(...)` -> `distanceToBiomeFlat(...)`
- `()m()` -> `min(x, y)`
- `()M()` -> `max(x, y)`

Along with this, a few function arguments have changed.

- `distanceToBiome([Distance], [Biome Name]...)`
- `distanceToBiomeFlat([Distance], [Y Position], [Biome Name]...)`

## Color Entries
Color entries have received a few small changes.
- `alpha` has been renamed to `ratio`
- `inherit` has been removed. Instead, use the respective color variable (`#skyColor`, `#fogColor`).

## Celestial Objects
Blank values in Celestial objects have been given new default values that are greater than zero. Some populate objects (ex. stars in the demo packs) may rely on these values defaulting to zero, so it may be necessary to manually set these values to zero in the celestial object file.

## Twilight Settings
Twilight settings in the [environment settings](https://github.com/fishcute/Celestial/wiki/Environment-Details#entries) have been deprecated, and will be removed in a future update. An alternative solution for twilight has been added in this update, so it is recommended to use this if anything in your resource pack relies on these settings.

## Fog
`fog_start` and `fog_end` no longer use `-1` as a default value. Instead, they both use the `fogStart` and `fogEnd` variables respectively.
```
"fog": {
	"fog_start": "fogStart",
	"fog_end": "fogEnd"
}
```

## Final Notes
If you have any difficulties updating your resource pack, or even have the smallest questions, do not be afraid to ask for help on the [discord](https://discord.gg/9KsHkDE6u2)!