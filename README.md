PixelTaskTypes is a Spigot plugin developed for Arclight servers, enabling integration of
Pixelmon specific tasks and events for [LMBishop's Quests plugin](https://github.com/LMBishop/Quests).
With this plugin, server owners can create endless combinations of engaging and challenging quests for their Pixelmon servers.

## Features

- Seamless integration with Pixelmon and LMBishop's Quests
- Support for multiple task types, including catching, defeating, evolving and hatching Pok├®mon.
- Highly customizable task configurations.
- Simple installation, no additional configuration required.

## Dependencies

- Arclight Forge for Minecraft 1.16.5
- Pixelmon for 1.16.5
- LMBishop's Quests

## Installation

- Place the `PixelTaskTypes-<version>.jar` file in your Arclight server's `plugins` folder.
- Start/Restart the server.
- All done!

## Configuration

PixelTaskTypes has no configuration of its own, it instead extends LMBishop's Quests with additional task types to
allow Pixelmon related quests to be created.
Please refer to [their documentation](https://quests.leonardobishop.com/configuration/) for a full configuration setup.

The section below will showcase and explain the added task types.
As this is just an extension, all existing quest configuration can be combined with the Pixelmon tasks to create even more diverse and unique quests.

### All Pixelmon Task Types

These fields are available for all Pixelmon-related tasks.

**Note**: Certain combinations can make a task impossible to complete.
For example, setting `species` to `Pikachu` and `legendary_only` to `true` creates an invalid task,
as Pikachu is not a legendary Pok├®mon and thus the progression criteria can never be met.

#### Required

- `amount` - Number of times the task must be completed.

#### Optional

- `species` - List of Pok├®mon species that count toward progress.
- `not_species` - List of Pok├®mon species that will not count toward progress.
- `pokemon_types` - List of Pok├®mon types that count toward progress.
- `palettes` - List of Pok├®mon palettes that count toward progress. (See the [Pixelmon Wiki](https://pixelmonmod.com/wiki/Form_indices) for available palettes.)
- `legendary_only` - If `true`, only legendary Pok├®mon count toward progress.
- `pokemon_level` - Minimum level required for Pok├®mon to count.

### Catch Pok├®mon

Progress is made by catching Pok├®mon with Pok├®balls.

#### Optional

- `poke_balls` - A list of Pok├®balls that need to be used to progress the task.

#### Simple Example

Catch 20 Pok├®mon, no additional requirements.

```yml
type: "catch_pokemon"
amount: 20
```

#### Advanced Example

Catch a legendary Pok├®mon using a park ball or a master ball, not counting Articuno, Zapdos, or Moltres.

```yml
type: "catch_pokemon"
amount: 1
legendary_only: true
poke_balls:
	- "master_ball"
	- "park_ball"
not_species:
	- "articuno"
	- "zapdos"
	- "moltres"
```

### Defeat Pok├®mon

Progress is made by defeating Pok├®mon in battles.

#### Optional

- `wild_only` - If `true`, only wild Pok├®mon count.
- `pvp_only` - If `true`, only Pok├®mon defeated in PvP battles count.

#### Simple Example

Defeat 20 Pok├®mon, either in the wild or in PvP.

```yml
type: "defeat_pokemon"
amount: 20
```

#### Advanced Example

Defeat 10 Fire or Ice type Pok├®mon in the wild that are level 30 or higher.

```yml
type: "defeat_pokemon"
amount: 10
wild_only: true
pokemon_types:
	- "fire"
	- "ice"
level: 30
```

### Evolve Pok├®mon

Progress is made by evolving Pok├®mon.

#### Optional

- `evolution_types` - Specify required evolution methods. Options:
  - `leveling` - Evolve through leveling up.
  - `interaction` - Evolve via items (e.g., Leaf Stone).
  - `tick` - Passive evolution (e.g., happiness or time-based).
  - `trading` - Evolve through trading.

#### Simple Example

Evolve 5 Pok├®mon.

```yml
type: "evolve_pokemon"
amount: 5
```

#### Advanced Example

Evolve 3 Pok├®mon through leveling or trading.

```yml
type: "evolve_pokemon"
amount: 3
evolution_types:
	- "leveling"
	- "trading"
```

### Hatch Eggs

The `hatch_egg` task type progresses the quest when players hatch Pok├®mon eggs. There are no extra fields as of now.

#### Simple Example

Hatch 10 eggs.

```yml
type: "hatch_egg"
amount: 10
```

#### Advanced Example

Hatch a shiny Zorua or Eevee from an egg.

```yml
type: "hatch_egg"
amount: 1
palettes:
	- "shiny"
species:
	- "eevee"
	- "zorua"
```

