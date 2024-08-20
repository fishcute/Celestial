# Note: As of the 1.2 update, this feature has been removed.

[Populate celestial objects](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#populate) with a solid color can be used to render stars instead.

***

The environment details section in [`sky.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson) allows resource packs to change environmental details, such as fog, sky colors, and other various things.

***

### Entries:
- `rotation`, _JSON Object_: Controls the rotation of the stars. [[More details]](https://github.com/fishcute/Celestial/wiki/Rotation-Entry)
- `count`, _Integer_: Number of stars in the sky.
- `brightness`, _Equation_: Alpha of stars.
- `min_size`, _Float_: Minimum size of stars.
- `max_size`, _Float_: Maximum size of stars.
- `colors`, _String list_: List of hex colors of stars.

Variables for `brightness`:
- #starAlpha: Brightness of stars