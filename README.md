# Trakteer Functions
Fabric mod for triggering mcfunctions from Trakteer donations.

When the donation has an action ID specified (by adding `[id:n]` to the support_message where `n` is a number)  
`/function re:command/{unit_name}/{wuantity}/{id}.mcfunction` will be run.  
For example `/function re:command/creeper/1/5.mcfunction`.

When no ID is specified, `/function re:command/noid.mcfunction` will be run.

## Configuration
To get the value of any config option run `/trakteer-functions <optionName>`  
To set the value of a config option run `/trakteer-functions <optionName> <value>`

| Option Name | Values/Type       | Default Value | Description                        |
|-------------|-------------------|---------------|------------------------------------|
| apiKey      | string            | none          | The API key to access Trakteer API |
| interval    | integer (seconds) | 0 (disabled)  | Interval in seconds for polling    |
| mode        | test\|real        | test          | Specifies which API to connect to  |

## Testing API
The testing API can be found in the [testing-api branch](https://github.com/arnokeesman/Trakteer-Functions/tree/testing-api)
