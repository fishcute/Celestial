All `rotation` entries have a common format. This will go over that format.

### Entries:
- `degrees_x`, _Equation_: The X degrees of the rotation.
- `degrees_y`, _Equation_: The Y degrees of the rotation.
- `degrees_z`, _Equation_: The Z degrees of the rotation.
- `base_degrees_x`, _Equation_: The base X degrees of the rotation.
- `base_degrees_y`, _Equation_: The base Y degrees of the rotation.
- `base_degrees_z`, _Equation_: The base Z degrees of the rotation.

### Base degrees
Base degrees are the rotations applied before any transformations occur. This allows for more control over rotations, but does not need to be used for basic rotations. By default, the base rotation is -90, 0, -90.

