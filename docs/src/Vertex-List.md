Vertex lists allow you to customize the shape and UV mappings of an object's vertex. 

Vertex lists consist of [vertex points](https://github.com/fishcute/Celestial/wiki/Vertex-List#vertex-points).

Example:
```
"vertex": [
	{ "x": "-100", "y": "50", "z": "-100", "uv_x": 0, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": "-100", "uv_x": 1, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": " 100", "uv_x": 1, "uv_y": 1 },
	{ "x": "-100", "y": "50", "z": " 100", "uv_x": 0, "uv_y": 1 }
]
```

This would render a textured object that has a size of 100, and a distance of 50.

***

## Vertex points

Vertex points are entries in a vertex list, and are formatted as JSON objects.

### Entries:

- `x`, _Equation_: The X position of the vertex.
- `y`, _Equation_: The Y position of the vertex.
- `z`, _Equation_: The Z position of the vertex.
- `uv_x`, _Equation_: The X position of the UV (Optional).
- `uv_z`, _Equation_: The Z position of the UV (Optional).

If an object's [type](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry#object-types) is `color`, the `uv_x` and `uv_z` values do not need to be present.

***

### Examples

Renders a square with a texture.

```
"vertex": [
	{ "x": "-100", "y": "50", "z": "-100", "uv_x": 0, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": "-100", "uv_x": 1, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": " 100", "uv_x": 1, "uv_y": 1 },
	{ "x": "-100", "y": "50", "z": " 100", "uv_x": 0, "uv_y": 1 }
]
```

Renders a right triangle with a texture.

```
"vertex": [
	{ "x": "-100", "y": "50", "z": "-100", "uv_x": 0, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": "-100", "uv_x": 1, "uv_y": 0 },
	{ "x": " 100", "y": "50", "z": " 100", "uv_x": 1, "uv_y": 1 },
	{ "x": "0", "y": "0", "z": "0", "uv_x": 0, "uv_y": 0 }
]
```

Renders a square without a texture.

```
"vertex": [
	{ "x": "-100", "y": "50", "z": "-100" },
	{ "x": " 100", "y": "50", "z": "-100" },
	{ "x": " 100", "y": "50", "z": " 100" },
	{ "x": "-100", "y": "50", "z": " 100" }
]
```
