# Army Simulator

A console application for assembling two factions' armies from a shared unit catalog and
simulating battles between squads. Built for the M1 Java OOP final project.

## Purpose

You build an `Army` (Alliance, Horde or Neutral) out of `Group`s of units, under a fixed
point budget. Units are not typed in by hand: they're picked from a catalog of unit
templates (name, cost, attack, defense, health) that is loaded from a CSV file at startup.
Once an army has at least two groups, you can simulate a battle between any two of them and
get a round-by-round combat log and a winner.

## How to run

The project uses Maven.

```
mvn compile exec:java
```

or open the project in IntelliJ (it will detect `pom.xml`) and run
`com.armysimulator.Application`.

## How to run the tests

```
mvn test
```

## Main features

- Create an army with a faction and a point budget.
- Add/remove groups and units; units are selected from the loaded catalog, and adding one
  that would exceed the remaining budget is rejected.
- Display the army with its groups, units, stats and point usage.
- Simulate a battle between two groups: units trade damage round by round (attack vs.
  defense) until one side has no units left standing, or a round cap is hit (draw).

## Main classes

- `model.Unit` (abstract) / `model.Infantry` / `model.Vehicle`: the unit hierarchy.
- `model.Combatant`: the combat contract implemented by `Unit`.
- `model.UnitTemplate`: one catalog row (a unit blueprint).
- `model.Group` / `model.Army`: squad and army membership.
- `service.UnitCatalogService`: in-memory view of the loaded catalog, grouped by category.
- `service.ArmyBuilderService`: budget validation, unit creation from a template, and
  catalog check at startup.
- `service.CombatService`: battle resolution (the core business logic).
- `util.UnitCatalogLoader`: reads `units_catalog.csv`.
- `util.ConsoleInputReader` / `util.ArmyPrinter`: console I/O, kept out of the domain model.
- `Application`: entry point; `main` wires the objects together and the menu loop delegates
  every rule to the services.

## Technical decisions

1. **Stats live in the catalog file, not in Java code.** `InfantryType`/`VehicleType` are
   pure classification enums; attack/defense/health/cost come from `UnitTemplate` rows
   loaded at startup. This was the only way to make "load data from a file" a real
   requirement rather than a cosmetic one — otherwise the file would just mirror constants
   already hardcoded in the enums.
2. **Custom exceptions are checked**, extending `ArmySimulationException extends Exception`.
   This forces every call site to handle business errors explicitly with try/catch, instead
   of risking a silently-ignored `RuntimeException`.
3. **Combat has a round cap (50).** Two sides with very high defense and low attack could
   otherwise never reduce each other to zero health, looping forever. The cap turns that
   into a well-defined draw instead of an infinite loop, and is directly covered by a test.
4. **Presentation is separated from the model.** The original prototype printed
   ANSI-colored output directly from `Army` and `Unit`. Model classes now only return
   plain text (`Unit.describe()`); colors and console output live in `util.ArmyPrinter`.
5. **The catalog is validated at startup.** When the catalog is loaded,
   `ArmyBuilderService.checkCatalog()` builds every row once as a dry run, so an invalid row
   (unknown category/subtype, negative stats...) stops the application with a clear
   message at startup instead of failing in the middle of a game. The value rules
   themselves live in the model constructors, so they are written only once.

## Where inheritance is used

`Infantry` and `Vehicle` both extend the abstract class `model.Unit`, which holds the state
shared by every unit (name, cost, base attack/defense, health) and its validation rules,
and leaves the computation of actual combat power to the subclasses.

## Where polymorphism is used

- `service.CombatService` calls `getAttackPower()` / `getDefensePower()` on whatever
  `Unit` it is given, without knowing whether it is an `Infantry` or a `Vehicle`. Each
  subclass computes them differently:
  - **Infantry**: wounded soldiers fight less well. Below half health, attack is halved.
    Defense (equipment) does not change.
  - **Vehicle**: keeps its full firepower until destroyed, but its armor degrades. Defense
    scales with the remaining health ratio.
- `Unit.describe()` is implemented differently by each subclass and used by `ArmyPrinter`.

## Where the abstract class/interface is used

- `model.Unit` is an **abstract class**: it gives `Infantry`/`Vehicle` shared state and
  behavior (stat validation, health tracking, damage application) and forces them to
  define how they fight (`getAttackPower`, `getDefensePower`) and how they describe
  themselves.
- `model.Combatant` is an **interface**: it defines the minimal combat contract
  (attack/defense/damage/defeated) independently of the army-management state that `Unit`
  carries, so a future combat participant that isn't a roster `Unit` (e.g. a fortification)
  could implement it without inheriting from `Unit`.

## Custom exceptions

- `exception.ArmySimulationException` — base checked exception for all domain errors.
- `exception.InsufficientPointsException` — adding a group/unit would exceed the army's
  point budget.
- `exception.EmptyGroupException` — a battle was requested with a group that has no living
  units.
- `exception.InvalidUnitDataException` — the catalog file is missing, empty, or has a
  malformed row.

Standard exceptions are also used: `IOException` (reading the catalog file),
`NumberFormatException` (console input and CSV parsing), `IllegalArgumentException`
(invalid values passed to model constructors, e.g. a non-positive budget or negative
damage), `NullPointerException` via `Objects.requireNonNull`, and
`IndexOutOfBoundsException` (invalid group/unit indices).

## How the application loads its data

At startup, `Application` reads `units_catalog.csv` (bundled as a classpath resource under
`src/main/resources`) via `util.UnitCatalogLoader`, producing a list of `UnitTemplate`s.
The loader checks the file's shape (column count, numeric values). `ArmyBuilderService`
then builds every row once to make sure each one is a valid unit, and `UnitCatalogService`
groups the rows by category for the menu. Every unit you add to an army is built from one of these templates; there is
no hardcoded `new Infantry(...)` roster.

## How SOLID principles were applied

- **SRP**: model classes (`Unit`, `Group`, `Army`) hold state and their own invariants;
  `ArmyBuilderService` and `CombatService` hold business rules;
  `UnitCatalogLoader`, `ConsoleInputReader`, `ArmyPrinter` handle I/O; `Application` only
  wires objects and runs the menu.
- **OCP**: `CombatService` and `ArmyPrinter` work with any `Unit` subclass, so adding a new
  kind of unit does not change them. Only the `if/else` in `ArmyBuilderService.createUnit()`
  has to be extended, which was a deliberate choice to keep unit creation simple with only
  two unit types.
- **LSP**: `Infantry` and `Vehicle` are interchangeable wherever a `Unit`/`Combatant` is
  expected; they respect the same contract (non-negative stats, health between 0 and max)
  and the combat loop never checks the concrete type.
- **ISP**: `Combatant` only exposes combat-related methods, not the army-management ones.
- **DIP**: `CombatService` and `Group` depend on the `Unit`/`Combatant` abstractions, not on
  `Infantry`/`Vehicle`. `Application` receives all its collaborators through its
  constructor; they are created in one place (`main`).

## Data file

`src/main/resources/units_catalog.csv` — columns: `category,subtype,name,cost,attack,defense,health,capacity`.
`category` is `INFANTRY` or `VEHICLE`; `subtype` is `SOLDIER`/`HEAVY`/`SPECIAL`/`LEADER` for
infantry and `TRANSPORT`/`COMBAT` for vehicles (case-insensitive).
