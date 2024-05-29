# Trakteer Functions

[Bahasa Indonesia](https://github.com/arnokeesman/Trakteer-Actions/blob/main/README-id_ID.md)

Trakteer Functions is a Fabric mod designed to integrate Trakteer donations with your Minecraft world or server. This
mod allows players to create interactive and engaging experiences based on real-time donations from the Trakteer
platform. By configuring actions, server owners can customize how their Minecraft environment responds to different
donation events, enhancing the gameplay and fostering a more dynamic interaction between supporters and players.

Key features of Trakteer Functions include:

- Configurable Actions: Define specific actions that should occur in the game based on donation properties such as
  supporter name, donation amount, and custom messages.
- Real-time and Test Modes: Choose between live (real) mode to connect with the Trakteer API or test mode to simulate
  donations for configuration and testing purposes.
- Flexible Requirements: Use various operators to set conditions for actions, ensuring that donations trigger the
  desired in-game events only under certain criteria.

## Command options

### Options

To get the value of any config option run `/trakteermod <optionName>`  
To set the value of a config option run `/trakteermod <optionName> <value>`

| Option Name | Values/Type       | Default Value | Description                        | Role     |
|-------------|-------------------|---------------|------------------------------------|----------|
| interval    | integer (seconds) | 0 (disabled)  | Interval in seconds for polling    | Admin    |
| mode        | test\|real        | test          | Specifies which API to connect to  | Admin    |
| apiKey      | string            | none          | The API key to access Trakteer API | Everyone |

## Configuration

This section is only relevant to server owners  
Config can be reloaded using `/trakteermod reload`

### Syntax

- Start a new Action  
  `### <Name of action>`
- Mark action as available when player is offline  
  `:offline`
- Add requirements to an Action  
  `:if <donation property> <operator> <value>`
- Include commands from another Action  
  `:include <Name of Action to include>`
- Add Commands to run for action  
  Regular minecraft command without the /  
  See example config

### Donation properties

| Name              | Description               |
|-------------------|---------------------------|
| `supporter_name`  | Name of the supporter     |
| `support_message` | Message of the donation   |
| `amount`          | Amount Donated            |
| `unit_name`       | Name of donation unit     |
| `quantity`        | Amount of donation unit   |
| `receiver`        | Player receiving donation |

### Requirement operators

| Operator   | Alias | Description                                                      |
|------------|-------|------------------------------------------------------------------|
| `contains` |       | Property on the left contains string on the right                |
| `equals`   | `=`   | Property on the left is the same as value on the right           |
| `gte`      | `>=`  | Number on the left is greater than or same as value on the right |
| `lte`      | `<=`  | Number on the left is less than or same as value on the right    |
| `gt`       | `>`   | Number on the left is greater than value on the right            |
| `lt`       | `<`   | Number on the left is less than value on the right               |

### Example config

```
### default
:offline
say {supporter_name} donated {amount}!
give {receiver} minecraft:diamond 1
effect give {receiver} minecraft:regeneration 10 1

### kaboom
:if support_message contains boom
:if amount >= 10000
:include default
execute at {receiver} run summon minecraft:creeper ~ ~ ~ {ExplosionRadius:5,ignited:1}
say Watch out, {receiver}! A creeper is coming your way!

### splash
:if support_message contains splash
execute at {receiver} run setblock ~ ~1 ~ minecraft:water
effect give {receiver} minecraft:water_breathing 60 1
say {supporter_name} has given {receiver} a splash surprise!

### lightning
:if support_message contains strike
:if amount >= 2000
execute at {receiver} run summon minecraft:lightning_bolt
say {supporter_name} has struck {receiver} with lightning!

### feast
:if support_message contains feast
:if quantity >= 5
give {receiver} minecraft:cooked_beef 10
give {receiver} minecraft:golden_apple 1
say {supporter_name} has provided a feast for {receiver}!

### armor_up
:if support_message contains armor
give {receiver} minecraft:diamond_chestplate
give {receiver} minecraft:diamond_leggings
give {receiver} minecraft:diamond_boots
give {receiver} minecraft:diamond_helmet
say {supporter_name} has equipped {receiver} with diamond armor!

### fireworks
:if amount >= 5000
:include default
execute at {receiver} run summon firework_rocket ~ ~5 ~ {LifeTime:0,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Flight:2,Explosions:[{Type:1,Flicker:0,Trail:0,Colors:[I;11743532,14602026],FadeColors:[I;2437522]},{Type:1,Flicker:0,Trail:0}]}}}}
say A firework show for {receiver} courtesy of {supporter_name}!

```

## Testing API

To test the mod without needing real donation there's also a mock API for testing.  
You can run this on your own computer, to try Action configurations in singleplayer.  
Detailed explanation can be found in the README of the testing API.
The testing API can be found in
the [testing-api branch](https://github.com/arnokeesman/Trakteer-Actions/tree/testing-api)
