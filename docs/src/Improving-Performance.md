# This page is still being developed!
If you would like to suggest anything for here, feel free to create a suggestion in the issue tracker or message me on discord.

***

It is important to keep performance in mind when creating a resource pack. While Celestial will try to be relatively optimized, there are some things that can cause performance issues if used incorrectly or poorly.
***
## Improving Performance with Custom Variables
Variables can allow for complicated equations to only be calculated once per a specified frequency. Rather than having a complicated equation be calculated multiple times, a custom variable with a value set as the equation can drastically improve performance.

While equations themselves are not particularly performance heavy, some things such as [special variables](https://github.com/fishcute/Celestial/wiki/Equations#special-variables) can be more performance heavy. When using special variables for multiple things, it is strongly recommended to use a custom variable instead.

For example, say you want multiple celestial objects to appear when you are in a plains biome.

Instead of setting each object's value to something like this:

`"alpha": "#distanceToBiome(plains, 8)"`

`"alpha": "#distanceToBiome(plains, 8) / 2"`

`"alpha": "#distanceToBiome(plains, 8) + 0.1"`

You can create a custom variable and do something like this instead:

```
"variables": [
  	{
		"name": "customVariable",
		"value": "#distanceToBiome(plains, 8)"
	}
]
```

`"alpha": "#customVariable"`

`"alpha": "#customVariable / 2"`

`"alpha": "#customVariable + 0.1"`

This will improve performance drastically, as #distanceToBiome can be very performance heavy. It is recommended to do this for other custom variables too.

## Improving Equation Performance

While equations aren't particularly performance intensive, there are ways that they can be optimized. One way they can be optimized is by decreasing the amount of math that needs to be done. Along with this, equations that have single values (such as `10`, or `#skyAngle`) can be better for performance. 

For example, instead of doing something like this:

`"distance": "10 + 5 + 2"`

`"alpha": "0.5 * 2"`

`"pos_x": "15 / 5"`

This should be done instead: 

`"distance": "17"`

`"alpha": "1"`

`"pos_x": "3"`

## Improving Populate Object Performance

There are many advantages to using populate objects, but due to their ability to create a lot of objects, they can also easily cause performance issues.

Below is a list of things that could impact performance negatively (Do not do these):
- A high count populate object using a texture (Causes FPS issues due to many textures being rendered)
- An extremely high count populate object
- A high count populate object using `per_object_calculations`

It is recommended to not use `per_object_calculations` when having a high count of populate objects. If for example you set `per_object_calculations` to true and have a populate count of 1000, that means the mod would have to do 1000 more calculations per tick.

***

Keep in mind these are only suggestions for improving performance. They do not need to be followed but are strongly recommended.