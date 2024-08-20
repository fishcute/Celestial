The environment details section in [`sky.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson) allows resource packs to change environmental details, such as fog, sky colors, and other various things.

***

### Entries:
- `fog_color`, _Color Entry_: The hex color of fog.
- `sky_color`, _Color Entry_: The hex color of the sky.
- `twilight_color`, _Color Entry_: The hex color of sunrises/sunsets.
- `twilight_alpha`, _Equation_: The alpha value of sunrises/sunsets.
- `clouds`, _JSON Object_: Details for cloud rendering. [[More details]](https://github.com/fishcute/Celestial/wiki/Environment-Details#cloud-details)
- `fog`, _JSON Object_: Details for fog rendering. [[More details]](https://github.com/fishcute/Celestial/wiki/Environment-Details#fog-details)
- `void_culling_level`, _Equation_: How high or low the void culling effect should appear.

***

## Cloud Details

### Entries:
- `height`, _Equation_: The y-level of clouds.
- `color`, _Color Entry_: The hex color of clouds.

***

## Fog Details

### Entries:
- `has_thick_fog`, _Boolean_: Use nether fog.
- `fog_start`, _Equation_: The start distance of fog. Will not have an effect if `has_thick_fog` is enabled.
- `fog_end`, _Equation_: The end distance of fog. Will not have an effect if `has_thick_fog` is enabled.

(Will be changed soon) To use default fog, set `fog_start` and `fog_end` to `-1`