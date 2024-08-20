Celestial Objects can be registered in [`sky.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson)

***

Registered celestial objects must be located in `assets/celestial/sky/(Dimension ID)/objects`, otherwise they will not be loaded. Celestial objects can also be located in folders within the `objects` folder.

If an object is located in a folder within the `objects` folder, the object's registration ID must start with the folder name.

Examples:

`assets/celestial/sky/overworld/objects/moon.json`, Registration ID: `moon`

`assets/celestial/sky/the_end/objects/folder/end_sun.json`, Registration ID: `folder/end_sun`

Objects cannot have the same name (even if they are in different folders), otherwise they will be overridden.

***

Celestial objects are things that are rendered in the sky. The vanilla sun and moon are examples of celestial objects.

### Entries:
- `type`, _String_: The type of object the object is. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#object-types)
- `texture`, _File path_: The file path to the texture of the object. If this value is present, and the `type` entry is empty, the object's [type](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#object-types) will automatically get set to `texture`.
- `skybox`, _JSON Object_: Data for skybox rendering. Will only get read if the object's [type](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#object-types) is `skybox`. [[More details]](https://github.com/fishcute/Celestial/wiki/Skybox-Objects)
- `solid_color`, _String_: Renders the object as a solid color (the color inputted here). If this value is present, and the `type` and `texture` entries are empty, the object's [type](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#object-types) will automatically get set to `color`.
- `display`, _JSON Object_: Details for display. If `vertex` has a value, this will be ignored. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#display)
- `vertex`, _String list_: Details for custom vertex rendering. [[More details]](https://github.com/fishcute/Celestial/wiki/Vertex-List)
- `rotation`, _JSON Object_: Controls the rotation of the object. [[More details]](https://github.com/fishcute/Celestial/wiki/Rotation-Entry)
- `properties`, _JSON Object_: Object properties. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#properties)
- `populate`, _JSON Object_: Controls the populate feature for the object. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#populate)

***

## Display

### Entries:
- `scale`, _Equation_: Scale of the object.
- `pos_x`, _Equation_: Added position X of the object.
- `pos_y`, _Equation_: Added position Y of the object.
- `pos_z`, _Equation_: Added position Z of the object.
- `distance`, _Equation_: Distance of the object to the camera.

***

## Object types

### `skybox`: 
Renders the object as a [skybox](https://github.com/fishcute/Celestial/wiki/Skybox-Objects).
### Entries:
- `texture`, _String_: Texture of the skybox (Optional).

### `color`: 
Renders the object with a solid color.
### Entries:
- `solid_color`: _Color Entry_: Color of the skybox.

### `texture`:
Renders the object with a texture.
### Entries:
- `texture`, _String_: Texture of the object.

***

## Properties

Extra miscellaneous properties for objects

### Entries:
- `has_moon_phases`, _Boolean_: Determines if the object has moon phases. Texture should be formatted like vanilla moon_phases texture.
- `moon_phase`, _Equation_: The moon phase of the object. Only works if `has_moon_phase` is enabled.
- `is_solid`, _Boolean_: Determines if other objects can render through the object. Removes alpha from the object too.
- `red`, _Equation_: Amount of red in object texture.
- `green`, _Equation_: Amount of green in object texture.
- `blue`, _Equation_: Amount of blue in object texture.
- `alpha`, _Equation_: Alpha of object.
- `ignore_fog`, _Boolean_: Determines if the object should render with fog applied.

If an object is a [populate object](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#populate), and has `per_object_calculations` enabled, the following variables may be used in equations:
- #populateDegreesX: The rotation X added onto the populate object's rotation X.
- #populateDegreesY: The rotation Y added onto the populate object's rotation Y.
- #populateDegreesZ: The rotation Z added onto the populate object's rotation Z.
- #populatePosX: The pos X added onto the populate object's pos X.
- #populatePosY: The pos Y added onto the populate object's pos Y.
- #populatePosZ: The pos Z added onto the populate object's pos Z.
- #populateDistance: The distance added onto the populate object's distance.
- #populateScale: The scale added onto the populate object's scale.
- #populateId: The populate object number (Will be from 0 to the `count` of populate objects).

***

# Populate

Allows for multiple duplicate objects to be rendered in different places, scales, and rotations. If enabled, the `display` and `rotation` categories will be ignored.
Populate objects cannot be a skybox.

### Entries:
- `count`, _Integer_: Number of objects to create.
- `rotation`, _JSON Object_: Rotation of objects in sky. Unlike normal rotation entries, this has different possible values. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#rotation-populate)
- `display`, _JSON Object_: Controls what the objects will look like. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#display-populate)
- `objects`, _JSON Object List_: Allows for the creation of populate objects with specified values. [[More details]](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#objects-populate)
- `per_object_calculations`: Determines if the rotation, position, etc. are calculated for each populate object.

*As of the 1.2 update, the rotation, position, and other things are calculated once, and applied to every single populate object. With `per_object_calculations`, the rotation, position, and other things are calculated for each populate object.*

## Rotation (Populate)

Values from the original `rotation` category will be added onto these values.

### Entries:
- `min_degrees_x`, _Float_: The minimum degrees X objects can have.
- `max_degrees_x`, _Float_: The minimum degrees X objects can have.
- `min_degrees_y`, _Float_: The minimum degrees Y objects can have.
- `max_degrees_y`, _Float_: The minimum degrees Y objects can have.
- `min_degrees_z`, _Float_: The minimum degrees Z objects can have.
- `max_degrees_z`, _Float_: The minimum degrees Z objects can have.

## Display (Populate)

### Entries:
- `min_scale`, _Float_: The minimum scale objects can have.
- `max_scale`, _Float_: The maximum scale objects can have.
- `min_pos_x`, _Float_: The minimum added position X the object can have.
- `max_pos_x`, _Float_: The maximum added position X the object can have.
- `min_pos_y`, _Float_: The minimum added position Y the object can have.
- `max_pos_y`, _Float_: The maximum added position Y the object can have.
- `min_pos_z`, _Float_: The minimum added position Z the object can have.
- `max_pos_z`, _Float_: The maximum added position Z the object can have.
- `min_distance`, _Float_: The minimum distance to the camera the object can have.
- `max_distance`, _Float_: The maximum distance to the camera the object can have.

## Objects (Populate)

The object list is a JSON Object list. Contains [populate object entries](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#populate-object-entry-populate).

Will be added on to existing populate objects created using the default populate object creation system.

Example:

```
{
	"populate": {
		"objects": [{
				"degrees_x": 0,
				"degrees_y": 0,
				"degrees_z": 0,
				"scale": 0
			},
			{
				"degrees_x": 30,
				"degrees_y": 0,
				"degrees_z": 30,
				"scale": 10
			}
		]
	}
}
```

## Populate object entry (Populate)

An entry for a populate object with specific values. Will still have values from the original `rotation` value added on, and will ignore values in the [populate rotation entries](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#rotation-populate) and the [populate display entries](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#display-populate).

### Entries:
- `scale`, _Float_: The scale of the populate object
- `pos_x`, _Float_: The X position of the populate object.
- `pos_y`, _Float_: The Y position of the populate object.
- `pos_z`, _Float_: The Z position of the populate object.
- `degrees_x`, _Float_: The X rotation of the populate object.
- `degrees_y`, _Float_: The Y rotation of the populate object.
- `degrees_z`, _Float_: The Z rotation of the populate object.
- `distance`, _Float_: The distance of the populate object.

Example:
```
{
  	"pos_x": 0,
	"pos_y": 0,
	"pos_z": 0,
	"degrees_x": 45,
	"degrees_y": 0,
	"degrees_z": 0,
	"scale": 0
}
```