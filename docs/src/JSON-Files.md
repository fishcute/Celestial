All the JSON files that are used.

***

## dimensions.json

This is the file that will allow you to register skies for specific dimensions.

***

`dimensions.json` will only function if the file is located in `assets\celestial\sky`.

### Entries:
- `dimensions`, _String list_: The list of all dimensions to be loaded.

## variables.json

This is the file where [custom variables](https://github.com/fishcute/Celestial/wiki/Custom-Variables) can be registered.

***

`variables.json` will only function if the file is located in `assets\celestial\sky`.

### Entries:
- `variables`, _JSON Object list_: The list of all custom variables to be registered.

## sky.json

This is the file that will allow you to register skies for specific dimensions.

***

`sky.json` will only function if the file is located in `assets\celestial\sky\(Dimension ID)`.

### Entries:
- `sky_objects`, _String list_: The list of registration IDs for all [celestial object](https://github.com/fishcute/Celestial/wiki/Celestial-Object-Entry) to be rendered. The order of entries will determine which objects are rendered when (First in the list will be rendered first, making it render under every other object, and the last in the list will make it render last, making it render over everything). File names should not include ".json".
- `environment`, _JSON Object_: Details for environment rendering. [[More details]](https://github.com/fishcute/Celestial/wiki/Environment-Details#fog-details)