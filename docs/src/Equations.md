Some entries for various things accept Equation values. This will go over what can be done with equations.

***

Equations are strings that will be parsed into numbers. Equations can also be parsed as doubles.

Some examples of equations:

`"scale": "#yPos / 4"`

`"alpha": "#isUsing(minecraft:spyglass) | #isUsing(minecraft:glass)"` 

`"distance": "print(10 * #dayLight)"`

`"pos_x": 5`

Equations must be written as strings to use functions, variables, and mathematical expressions

## Functions

Currently, equations support a variety of math functions:
- sqrt(): Returns the square root of a number.
- sin(): Sine (Value is converted to radians)
- cos(): Cosine (Value is converted to radians)
- tan(): Tangent (Value is converted to radians)
- floor(): Rounds down a number to the closest integer.
- ceil(): Rounds up a number to the closest integer.
- round(): Rounds a number to the closest integer.
- abs(): Returns the absolute value of a number.
- radians(): Returns an angle converted to radians.
- arcsin(): Arcsine (Value is converted to radians)
- arccos(): Arccosine (Value is converted to radians)
- arctan(): Arctangent (Value is converted to radians)
- print(): Sends an actionbar message with whatever value is in the parenthesis, and returns the value too.
- printnv(): Sends an actionbar message with whatever value is in the parenthesis, but returns a value of 0.
- (a)^(b): Returns (a) to the power of (b).
- (a)m(b): Returns the smallest number. (Will be deprecated soon)
- (a)M(b): Returns the largest number. (Will be deprecated soon)
- (a)&(b): Returns 1 if a and b are equal to 1. Returns 0 if not.
- (a)|(b): Returns 1 if a or b are equal to 1. Returns 0 if not.
- (a)=(b): Returns 1 if a and b are equal to each other. Returns 0 if not.
- (a)>(b): Returns 1 if a is greater than b. Returns 0 if not.
- (a)<(b): Returns 1 if a is less than b. Returns 0 if not.

## Variables

Equations also have some variables that can be used.

### Player Variables
- #xPos: X position of the player
- #yPos: Y position of the player
- #zPos: Z position of the player
- #headYaw: Yaw of the player's head (-180° to 180°).
- #headPitch: Pitch of the player's head (-180° to 180°).
- #isLeftClicking: Returns 1 if the player is holding left-click. Returns 0 if not.
- #isRightClicking: Returns 1 if the player is holding right-click. Returns 0 if not.
- #isSubmerged: Checks if the player is submerged in water. Returns 1 if true, and 0 if false.
- [#isUsing(...)](https://github.com/fishcute/Celestial/wiki/Equations#isusingitem): Checks if the player is right-clicking with an item (Does not work with offhand yet). Returns 1 if true, and 0 if false.
- [#isHolding(...)](https://github.com/fishcute/Celestial/wiki/Equations#isholdingitem): Checks if the player is holding an item (Does not work with offhand yet). Returns 1 if true, and 0 if false.
- [#isMiningWith(...)](https://github.com/fishcute/Celestial/wiki/Equations#isminingwithitem): Checks if the player is left-clicking with an item (Does not work with offhand yet). Returns 1 if true, and 0 if false.
- [#distanceTo(...)](https://github.com/fishcute/Celestial/wiki/Equations#distancetox-y-z): Returns the distance from the player to a coordinate.
- [#isInArea(...)](https://github.com/fishcute/Celestial/wiki/Equations#isinareax1-y1-z1-x2-y2-z2): Returns 1 if the player is within an area defined by two coordinate points. Returns 0 if not.
- [#distanceToArea(...)](https://github.com/fishcute/Celestial/wiki/Equations#distancetoareax1-y1-z1-x2-y2-z2). Returns the distance from the player to the edge of an area.

### World Variables
- #dayLight: Gets the daylight of the world.
- #starAlpha: Gets the brightness of stars.
- #rainGradient: How faded in precipitation is.
- #getGameTime: Gets the total time of the game instance.
- #getWorldTime: Gets the total time of the world.
- #getDayTime: Gets the time of the day.
- #moonPhase: Returns the phase of the moon.
- #skyDarken: Returns the sky darken modifier.
- #lightningFlashTime: Returns the duration of the lightning flash effect.
- #thunderGradient: How faded in thunder is.
- #twilightAlpha: Returns how faded in the twilight effect is.
- #skyAngle: Angle of the sky (Max angle is 360°).
- #biomeTemperature: Returns the [temperature](https://minecraft.wiki/w/Biome#Temperature) of the player's current biome.
- #biomeDownfall: Returns the [downfall](https://minecraft.wiki/w/Biome#Downfall) of the player's current biome.
- #biomeHasSnow: Returns 1 if it can snow in the player's current biome. Returns 0 if not.
- [#isInBiome(...)](https://github.com/fishcute/Celestial/wiki/Equations#isinbiomebiome): Returns 1 if the biome at the player's location is equal to the biome provided. Returns 0 if not.
- [#distanceToBiome(...)](https://github.com/fishcute/Celestial/wiki/Equations#distancetobiomebiome-optional-searchradius) Returns the distance to the nearest provided biome (Within render distance). Does a three-dimensional search.
- [#distanceToBiomeIgnoreY(...)](https://github.com/fishcute/Celestial/wiki/Equations#distancetobiomeignoreybiome-optional-searchradius-optional-ylevel) Returns the distance to the nearest provided biome (Within render distance). Does a two-dimensional search (Better for performance).

### Math Variables
- #maxInteger: Returns the maximum integer possible.
- #pi: Returns the value of pi.
- #random: Returns a random double from 0 to 1.

### Real World Variables
- #localDayOfYear: Returns the local day of the year (Out of 365 days).
- #localDayOfMonth: Returns the local day of the month (Out of 28 to 31 days).
- #localDayOfWeek: Returns the local day of the week (Out of 7 days).
- #localMonth: Returns the local month of the year (Out of 12 months).
- #localYear: Returns the local year.
- #localSecondOfHour: Returns the local second of the hour (Out of 3600).
- #localMinuteOfHour: Returns the local minute of the hour (Out of 60).
- #localSecondOfDay: Returns the local second of the day (Out of 86400).
- #localMinuteOfDay: Returns the local minute of the day (Out of 1440).
- #localHour: Returns the local hour of the day (Out of 24).

### Other Variables
- #tickDelta: Gets the tick delta.


Some equations can also have unique variables for their own cases.

## Special variables
Unlike normal variables, special variables accept arguments that will determine the value of the variable.
***

### #isUsing(item)

Checks if the player is holding right-click with an item. Returns 1 if true, and returns 0 if false.

This variable must have an item ID in the parentheses. A namespace can be included (minecraft:), and items from other mods can be used.

Examples:
- #isUsing(minecraft:spyglass)
- #isUsing(stick)
- #isUsing(botania:tiny_potato)

***

### #isHolding(item): 

Checks if the player is holding an item. Returns 1 if true, and returns 0 if false.

This variable must have an item ID in the parentheses. A namespace can be included (minecraft:), and items from other mods can be used.

Examples:

- #isHolding(minecraft:air)
- #isHolding(diamond_sword)

***

### #isMiningWith(item): 

Checks if the player is holding left-click with an item. Returns 1 if true, and returns 0 if false.

This variable must have an item ID in the parentheses. A namespace can be included (minecraft:), and items from other mods can be used.

Examples:

- #isMiningWith(diamond_pickaxe)
- #isMiningWith(mekanism:atomic_disassembler)

***

### #distanceTo(x, y, z): 

Returns the distance (in blocks) to a point of coordinates.

This variable must have three numbers in the parentheses.

Examples:

- #distanceTo(0, 0, 0)
- #distanceTo(1000.0, 126, 15.5)

***

### #isInArea(x1, y1, z1, x2, y2, z2): 

Returns if the player is inside of a cube area. Returns 1 if true, and returns 0 if false.

This variable must have six numbers in the parentheses.

Examples:

- #isInArea(-1, -1, -1, 1, 1, 1)
- #isInArea(-500, -64, -500, 500, 319, 500)

***

### #distanceToArea(x1, y1, z1, x2, y2, z2): 

Returns the distance (in blocks) a player is to a cube area.

This variable must have six numbers in the parentheses.

Examples:

- #distanceToArea(-500, -1, -500, 0, 1, 0)
- #distanceToArea(-2.5, -2.5, -2.5, 2.5, 2.5, 2.5)

***

### #isInBiome(biome):

Returns 1 if the player is in the biome provided, and 0 if not.

This variable must have a biome name in the parentheses. A namespace can be included, and biomes from other mods can be used.

Examples:

- #isInBiome(plains)
- #isInBiome(twilightforest:thornlands)

*** 
### #distanceToBiome(biome, (Optional) searchRadius)

Returns how near the player is to a biome (Returns a value from 0 to 1).

This variable must have a biome name in the parentheses, but the second argument, searchRadius, is optional (Default is 6). A namespace for the biome argument can be included, and biomes from other mods can be used.

searchRadius determines the radius of the check. If this number is set to 3, it will search in a diameter of 6 blocks.

The search will also be done in a three dimensional area, which can makes this function quite memory intensive when searching large areas. For example, if the search radius is 16, it will search in a `32^3` area, or `32*32*32`, which is equal to 32768 blocks. As the search radius increases, this number will also increase.

For search radiuses above 15 blocks, it is **strongly** recommended to use the [#distanceToBiomeIgnoreY](https://github.com/fishcute/Celestial/wiki/Equations#distancetobiomeignoreybiome-optional-searchradius-optional-ylevel) variable.

Examples:
- #distanceToBiome(minecraft:desert)
- #distanceToBiome(byg:frosted_taiga, 3)

***
### #distanceToBiomeIgnoreY(biome, (Optional) searchRadius, (Optional) yLevel)

Returns how near the player is to a biome (Returns a value from 0 to 1), using a set Y level.

This variable must have a biome name in the parentheses, but the second and third arguments, searchRadius and yLevel, are optional. By default, the searchRadius is 6, and yLevel is the player's Y level. A namespace for the biome argument can be included, and biomes from other mods can be used.

searchRadius determines the radius of the check. If this number is set to 3, it will search in a diameter of 6 blocks.

This variable only does a two dimensional search, but is significantly better for performance when having larger search areas. If the radius is set to 20, the amount of blocks searched will be equal to `40^2`, or `40*40`, which is equal to 1600.

Can effectively search with radiuses up to 110.

Examples:
- #distanceToBiomeIgnoreY(river)
- #distanceToBiomeIgnoreY(minecraft:ocean, 60)
- #distanceToBiomeIgnoreY(minecraft:beach, 20, 80)

