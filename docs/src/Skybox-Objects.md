Skybox celestial objects allow you to create skyboxes easily.

***

Skybox objects are [celestial objects](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry) that have their `type` value set to `skybox`.

It is possible to make a skybox without using this method, but this method is probably more efficient for performance and effort.

Skybox data is stored in the `skybox` entry in [celestial object entries](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry).

Skybox objects will use all default [celestial object](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry) properties except for rotation, position (These may change in a future update), vertex, and population. Skyboxes will also use the `solid_color` entry if it is present, and if there are no given textures.

There are two types of skyboxes:

- Simple skyboxes: All sides of the skybox use a single texture. [[More Info]](https://github.com/fishcute/Celestial/wiki/Skybox-Objects#simple-skyboxes)
- Complex skyboxes: Sides can have different textures, UV mappings, or can be missing. [[More Info]](https://github.com/fishcute/Celestial/wiki/Skybox-Objects#complex-skyboxes)

The scale of skybox objects are still controlled by the `scale` value in `display`.

## Simple Skyboxes
Simple skyboxes allow for a low amount of customization, but can be very simple to create.

***

### Entries:
- `texture`, _File path_: The texture of the skybox. If this entry does not exist, it will attempt to get the texture from the celestial object `texture` entry instead.
- `uv_size`, _Equation_: The size of the UV of each side on the skybox.
- `texture_width`, _Equation_: (Optional) The width (in pixels) of the entire texture (Not the width of the side UV). Should be the UV size multiplied by 3.
- `texture_height`, _Equation_: (Optional) The height (in pixels) of the entire texture (Not the height of the side UV). Should be the UV size multiplied by 2.

If the `texture_width` and `texture_height` values are not defined, they will automatically be set to the texture width and height.

Each section of the texture corresponds to a side of the skybox.

Simple skybox textures are formatted like below:

![simpleskybox](https://user-images.githubusercontent.com/47741160/196559281-91c980ff-6c78-4aab-9949-8fb148fe97dd.png)

Example:
```
"skybox": {
	"texture": "celestial:sky/overworld/objects/skybox_texture.png",
	"uv_size": "16",
	"texture_width": "48",
	"texture_height": "32"
}
```

This would render a skybox that has a 48x32 texture, with each side's UV being 16x16.

## Complex Skyboxes
Complex skyboxes can be more complicated to make, as one might assume from the name.

***

In order to make a skybox a complex skybox, it must have a `sides` entry.

### Entries:
- `texture`, _File path_: The texture of the skybox (Optional).
- `texture_width`, _Equation_: (Optional) The width of the entire texture.
- `texture_height`, _Equation_: (Optional) The height of the entire texture.
- `sides`, _JSON Object list_: The list of the faces of the skybox [[More Details]](https://github.com/fishcute/Celestial/wiki/Skybox-Objects#skybox-face).

The name of the JSON element must be the ID of the side the element corresponds to.

Below shows which ID corresponds to which side:
- `0`: Bottom
- `1`: North
- `2`: South
- `3`: Up
- `4`: East
- `5`: West

Example:
```
"skybox": {
	"texture": "minecraft:textures/block/stone.png",
	"texture_width": "16",
	"texture_height": "16",
	"sides": {
		"0": {
			"uv_x": "0",
			"uv_y": "0",
			"uv_width": "16",
			"uv_height": "16"
		},
		"1": {
		  	"texture": "minecraft:textures/block/bricks.png",
			"uv_x": "0",
			"uv_y": "0",
			"uv_width": "8",
			"uv_height": "8"
		}
	}
}
```
This would render the bottom side of the skybox as the stone texture, and the northern side as the brick texture.

## Skybox Face
The sides of a complex skybox. Can have a different texture to the main skybox, and is more customizable.

***

A skybox face can either be for individual faces, or for every single face of the skybox.

If the UV width or height are greater than the size of the texture, the texture will repeat. For example, if you had a 8x8 skybox texture and set the UV width and height to 32, the face will be rendered as a 4x4 grid of that texture.

All faces of the skybox must have the same texture dimensions.

### Entries:
- `texture`, _File path_: The texture of the skybox face. If this is not present, the texture value from the skybox entry will be used.
- `uv_x`, _Equation_: The X starting point of the UV.
- `uv_y`, _Equation_: The Y starting point of the UV.
- `uv_width`, _Equation_: The width of the UV.
- `uv_height`, _Equation_: The height of the UV.

Skybox face ID numbers go from 0 to 5.

Skybox faces are formatted like this:
```
"0": {
	...
},
"1": {
	...
},
"2": {
	...
},
"3": {
	...
},
"4": {
	...
},
"5": {
	...
}
```

Skybox faces can also be formatted like this to apply to every face:
```
"all": {
	...
}
```

If any UV x starting point, y starting point, width, or height is less than 0, the face will not be rendered.

Example Skybox Face:
```
"all": {
	"texture": "minecraft:textures/item/egg.png",
	"uv_x": "0",
	"uv_y": "0",
	"uv_width": "16",
	"uv_height": "16"
}
```
This would render the minecraft egg texture on all sides of a skybox.

It might just be eggsactly what you need.