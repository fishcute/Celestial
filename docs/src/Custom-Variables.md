Custom variables allow you to decrease the number of calculations made, and make things look cleaner.

Custom variables can also be used to perform [special functions](https://github.com/fishcute/Celestial/wiki/Custom-Variables#variables-with-different-functions).

***

Custom variables can be registered in [`variables.json`](https://github.com/fishcute/Celestial/wiki/JSON-Files#variablesjson).
In this JSON file, you can register custom variables that you can use in equations.

### Entries:
- `variables`, _JSON Object list_: The list of [variables](https://github.com/fishcute/Celestial/wiki/Custom-Variables#variable) to register

Example:
```
"variables": [
  	{
		"name": "variableA",
		"value": "#skyAngle / 2"
	},
	{
		"name": "isUsingRangedWeapon",
		"value": "(#isUsing(bow) | #isUsing(crossbow)) | #isUsing(trident)"
	},
	{
		"name": "incrementValue",
		"value": "#incrementValue + 1",
		"update_frequency": 20
	}
]
```
This will create three variables. 
- The first variable is named "variableA", and it will return the angle of the sky divided by two.
- The second variable is named "isUsingRangedWeapon", and will return whether or not the player is using a bow, a crossbow, or a trident.
- The third variable is named "incrementValue", and every second it will increment its value by 1.

## Variable

Allows you to store values and make equations look more organized.

***

Variables will only be updated once per tick (unless the update_frequency value is greater than 0, then it will be updated after a number of ticks have passed).

Variables can be registered in the `variables` list.

[Pre-existing variables](https://github.com/fishcute/Celestial/wiki/Equations#variables) cannot be overridden. If a variable is named after a pre-existing variable, it will return the value of the pre-existing variable.

### Entries:
- `name`, _String_: The name of the variable. Does not need to include a # at the start. Example: If the variable name is "getObjectAlpha", the usable variable will be named "#getObjectAlpha".
- `value`, _Equation_: The value of the variable. Can include other custom variables and itself.
- `update_frequency`, _Integer_: How often the value of the variable will be updated (Higher values will make the variable update less frequently, but will improve performance).

### Incremental Values
Custom variables can have an incremental value. This can be achieved by including a variable in it's own value.

As an example, say we have a variable named "moonRotation". In order to make this variable have an incremental value, the variable must be included in it's own value along with another value to be incremented by.

```
{
	"name": "moonRotation",
	"value": "#moonRotation + 1"
}
```

Every tick, moonRotation will be incremented by 1. How often this variable is incremented by can be changed using `update_frequency`.

```
{
	"name": "moonRotation",
	"value": "#moonRotation + 1",
	"update_frequency": 20
}
```

Now this variable will only be updated every second.

If we wanted to add this value to another value, say "#skyAngle", we would not change moonRotation. Instead, we would want to create a new variable like this:
```
{
	"name": "moonRotationAngle",
	"value": "#moonRotation + #skyAngle"
}
```

If we were to add #skyAngle to the value of moonRotation, then the value of that variable would increment by the value of #skyAngle. With this variable above, it will not affect moonRotation, and both values will be added on to each other.

When a resource pack reload occurs, the value of the variable will get reset to its original value.

### Variables with Different Functions

Below is a list of different variables that can be used to perform useful functions. Multiple can be combined to perform specific functions.

In all these examples:
- `#this` is referring to the variable itself.
- `#condition` is referring to a condition (Ex. `#isUsing(minecraft:spyglass)`)
- `#deactivationCondition` is referring to the condition that will reset the trigger when active (Only for trigger variables).

All these examples assume that any condition variable will return either 0 or 1.


**Trigger Variable:** Remains at 1 once a condition is met, and will reset to 0 when a different condition is met.

`((#this + #condition)m1) * (1 - #deactivationCondition)`

**Conditional Increment Variable:** Increments while a condition is met.

`(#this + ([Increment Amount] * #condition))`

**Conditional Reset Increment Variable:** Increments until a condition is met, then resets to 0.

`(#this + [Increment Amount]) * (1 - #condition)`

**Capped Increment Variable:** Increments until the variable reaches a value, then resets to 0. Similar to Conditional Reset Increment Variables.

`(#this + [Increment Amount]) * (#this < [Variable Cap])`

**Toggle Variable:** Toggles between 1 and 0 when a condition is met. Note: This variable will continuously toggle while the condition is met. Using a Single Time Trigger Variable for a condition is recommended. 

`#this + ((#condition = 1) * 1 * (((#this = 1) * -1) + ((#this = 0) * 1)))`

**Single Time Trigger Variable:** Stays active for one update tick when a condition is met, then remains at 2 until a different condition is met.

`((#this + (#condition * (#this = 0)) + (1 * (#this = 1))) * (1 - (#deactivationCondition = 1))M(0))m2`

**Percent Chance Variable**: Returns 1 or 0 depending on a percent chance (Out of 100).

`#random < ([Percent Chance] / 100)`

**Goal Variable**: Increases/decreases by 1 every update tick until it reaches a goal number. Goal number must be a whole number.

`#this + ((#this) > ([Goal Number]) * -1) + ((#this) < ([Goal Number]) * 1)`

**Conditional Value Change Variable**: Sets the value of a variable when a condition is met.

`#this * (1 - #condition) + ([New Value] * #condition)`

### Conditions

Conditions that return either 1 or 0.

**Not Condition** Returns 1 if `#a` is not equal to 1

`1 - #a`

**Greater Than Condition**: Returns 1 if `#a` is greater than `#b`

`(#a) > (#b)`

**Less Than Condition**: Returns 1 if `#a` is less than `#b`

`(#a) < (#b)`

**Equals Condition**: Returns 1 if `#a` is equal to `#b`

`(#a) = (#b)`

**Greater Than or Equal to Condition**: Returns 1 if `#a` is greater than or equal to `#b`

`((#a) > (#b)) | ((#a) = (#b))`

**Less Than or Equal to Condition**: Returns 1 if `#a` is less than or equal to `#b`

`((#a) < (#b)) | ((#a) = (#b))`

**And Condition**: Returns 1 if `#a` and `#b` are both equal to 1. (Note: There is currently an issue with this condition, and any other conditions that use this condition)

`(#a) & (#b)`

Alternative:

`(#a) * (#b)`

**Or Condition**: Returns 1 if `#a` or `#b` are equal to 1.

`(#a) | (#b)`

**XOr Condition**: Returns 1 if `#a` or `#b` are equal to 1, but not if both are equal to 1.

`((#a) | (#b)) - ((#a) & (#b))`
