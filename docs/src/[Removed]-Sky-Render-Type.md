# Note: As of the 1.3 update, this feature has been removed.

[Skybox objects](https://github.com/fishcute/Celestial/wiki/Skybox-Objects) can be used to render skyboxes instead.

***

The sky render type can be set in [`sky.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson).

***

### Render types
Currently, there are two sky render types:
- `normal`: Renders the sky like the overworld (Solid color, twilight colors).
- `skybox`: Renders the sky like the end sky box (Box with repeating textures).
### Skybox
The `skybox` render type has an extra variable that sets the file path to the texture, named `skybox_texture`. This can be found in [`sky.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#skyjson).

