![logo](https://raw.githubusercontent.com/fishcute/Celestial/main/modpage/logo.png)

# This wiki is mostly updated for 2.0. However, new features added in 2.0 have not been documented yet.
**If there is something incorrect in the wiki, please let us know!**

A Minecraft mod for Fabric and Forge that allows you to customize the sky.

If you find anything missing or incorrect, please create an issue or ping me on the discord.

***

Celestial uses resource packs to customize the sky.

Celestial can be reloaded using F3-T, or by using the reload keybind, which is by default F10 (This will not reload textures. In order to apply changes to textures, use F3-T).

***

This will go over how to create your own resource pack for Celestial.

[Creating the `dimension.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#dimensionsjson)

[Adding dimension entries](https://github.com/fishcute/Celestial/wiki/Dimension-Entry)

[Creating the `sky.json` for a dimension](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson)

[Changing sky colors, fog, and cloud details](https://github.com/fishcute/Celestial/wiki/Environment-Details)

[Adding an object in the sky](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry)

[Populating the sky with multiple objects](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#populate)

[Creating a skybox](https://github.com/fishcute/Celestial/wiki/Skybox-Objects)

[Adding custom variables](https://github.com/fishcute/Celestial/wiki/Custom-Variables)

[Improving performance](https://github.com/fishcute/Celestial/wiki/Improving-Performance)

All files for Celestial must be located in `assets\celestial\sky` 

***

## Entry types:

When looking at this wiki, you might see a list of entries. Entry types are the italicized text in the list entries.

This will go over what each entry type is.

### String
A character, or text.

Example:
 
```
"type": "skybox"
```
### Boolean
A true or false value.

Example:
 
```
"has_moon_phases": true
```
### Integer
A number without a decimal point.

Example: 

```
"update_frequency" : 20
```
### Double
A number with a decimal point.

Example: 

```
"min_scale": 10.5
```
### String list
A list of strings/text. Holds multiple values.

Example:
```
"dimensions": [
	"overworld",
	"the_end"
]
```
### JSON Object
A json element. There will usually be an explanation available as to how the JSON object should be formatted.

Example:
```
"skybox": {
	"uv_size": "16",
	"texture_width": "48",
	"texture_height": "32"
}
```
### JSON Object List
A list of JSON objects. There will usually be an explanation available as to how the list should be formatted. Holds multiple values.

Example:
```
"colors": [
	{
		"color": "ff0000",
		"alpha": "1 - dayLight"
	},
	{
		"color": "0800ff",
		"alpha": "holding(stone)"
	}
]
```
### Equation
Can be entered as a double or a string. If entered as a string, it will be solved as a math equation. [[More Details]](https://github.com/fishcute/Celestial/wiki/Equations)

Examples:
```
"scale": "(dayLight + 10) / 2"
```
Or...
```
"scale": 10.0
```

### Color Entry
Can be entered as a string, or a JSON object. If entered as a string, value must be a HEX color code. [[More Details]](https://github.com/fishcute/Celestial/wiki/Color-Entry)

Examples:
```
"sky_color": "#ffffff"
```
Or...
```
"sky_color": "00ffff"
```
Or...
```
"sky_color": {
	"update_frequency": 0,
	"base_color": "#ffffff",
	"colors": [{
			"color": "0000ff",
			"ratio": "dayLight"
		}
	]
}
```
### File Path
The path to a texture file. Should include a namespace at the start.

Example:
```
"texture": "minecraft:textures/block/diamond_block.png"
```

***

## Outdated Wiki Pages:

[Changing star details (1.0.1-)](https://github.com/fishcute/Celestial/wiki/%5BRemoved%5D-Star-Details)

Why this was removed: In versions above 1.0.1, stars can be created using [celestial objects](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry).

[Sky render types (1.2-)](https://github.com/fishcute/Celestial/wiki/%5BRemoved%5D-Sky-Render-Type)

Why this was removed: In versions above 1.2, [skybox objects](https://github.com/fishcute/Celestial/wiki/Skybox-Objects) can now be created.

***

(Not Up to Date) Download the template resource pack [here](https://github.com/fishcute/Celestial/raw/forgefabric-1.2-1.19/TemplateResourcePack1.2.zip)

The template resource pack includes all vanilla features (Nothing should appear different), and allows for them to be customized or removed.

Confused, or don't know what to do? [Check out the demo packs](https://www.curseforge.com/minecraft/texture-packs/celestial-demo-packs)

If you need assistance, or have a question about something, feel free to [ask on the discord](https://discord.gg/9KsHkDE6u2).
