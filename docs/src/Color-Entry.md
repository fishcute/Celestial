Color entries allow for more customization for colors of objects.

***

Color entries can be formatted in two ways:

`(name): "#ffffff"`

Or as a JSON Object:

`(name): {}`

The first method of formatting color entries allows you to set a simple color that won't be changed in any way. This method will usually be better for performance.

The second method is a complex color entry. Complex color entries allow for changing colors and more complicated things.

Color entries can also have their value set to either `#skyColor` or `#fogColor`. If the value is set to one of these, it will take either the sky color or fog color depending on which one was used.

## Complex Color Entries

Complex color entries allow you to set a base color, and color overrides.

***

### Entries:

- `base_color`, _String/HEX Color_: The base color that other colors will be added onto. Can be set to `inherit` to inherit the original color (ex. If this color entry is used for the sky color, it will inherit the default sky color).
- `colors`, _JSON Object list_: The list of [color overrides](https://github.com/fishcute/Celestial/wiki/Color-Entry#color-override).
- `update_frequency`, _Integer_: How often the color will be updated (Higher values will make the color update less frequently, but will improve performance).
- `red`, _Equation_: How much red will be present in the color.
- `green`, _Equation_: How much green will be present in the color.
- `blue`, _Equation_: How much blue will be present in the color.
- `ignore_sky_effects`, _Boolean_: Whether or not the color should change depending on various environmental factors (night, rain, wither effect). This entry will only work for `fog_color`, `sky_color`, and the `color` entry in the cloud environmental details section.

Example:
```
"color": {
	"update_frequency": 0,
	"base_color": "#ffffff",
	"colors": [{
			"color": "ff0000",
			"alpha": "1 - #dayLight"
		},
		{
			"color": "0800ff",
			"alpha": "#isHolding(stone)"
		}
	]
}
```

This color would be fully white in the morning, and get more red as the night comes. If the player is holding a block of stone, the color will turn blue.

Another Example:

```
"fog_color": {
	"update_frequency": 0,
	"base_color": "inherit",
	"colors": [{
			"color": "c4ac6a",
			"alpha": "#distanceToBiome(desert)"
		}
	]
}
```

If this were to be used as the fog color, it would use the default fog color and make the fog turn more sand-colored as the player goes closer to a desert.

## Color Override

An entry in the colors list.

***

### Entries:
- `color`, _String/HEX Color_: The color that will override the base color.
- `alpha`, _Equation_: The alpha/prominence of the color. If this value is equal to 1, it will completely override the base color and any other color entries before this entry. If this value is 0, it will not be present at all.

