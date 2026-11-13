# Army Simulator

A console application for building an army from a unit catalog and simulating battles
between the groups (squads) of that army. Built for the M1 Java OOP final project.

## Purpose

You build one `Army` (Alliance, Horde or Neutral) out of `Group`s of units, under a fixed
point budget. Units are not typed in by hand: they're picked from a catalog of unit
templates (name, cost, attack, defense, health) that is loaded from a CSV file at startup.
Once the army has at least two groups, you can simulate a battle between any two of its
groups and get a round-by-round combat log and a winner.

The application manages **one army at a time**. Choosing "Create an army" again replaces the
current army; the application asks for confirmation first, because the old army is lost.
The faction is a label of the army: battles are training fights between two squads of the
same army, not army-versus-army wars.

## Requirements

- **Java 21** (set by `maven.compiler.release` in `pom.xml`).
- **Maven 3** (or IntelliJ IDEA, which bundles it).

## How to run

The Maven project is in the `Projet_M1_POO_Java/` sub-folder of the repository, so go there
first:

```
cd Projet_M1_POO_Java
mvn compile exec:java
```

Or open the `Projet_M1_POO_Java/` folder in IntelliJ (it detects `pom.xml`) and run
`com.armysimulator.Application`.

## How to run the tests

From the same `Projet_M1_POO_Java/` folder:

```
mvn test
```

## Main features

- Create an army with a faction and a point budget (with confirmation if one already exists).
- Add/remove groups and units; units are selected from the loaded catalog, and adding one
  that would exceed the remaining budget is rejected.
- Display the army with its groups, units, stats and point usage.
- Simulate a battle between two groups: units trade damage round by round (attack vs.
  defense) until one side has no units left standing, or a round cap is hit (draw).

## Main classes

- `model.Unit` (abstract) / `model.Infantry` / `model.Vehicle`: the unit hierarchy.
- `model.Combatant`: the combat contract implemented by `Unit`, used by `CombatService`.
- `model.UnitCategory`: `INFANTRY` / `VEHICLE`, the main kind of a catalog row.
- `model.UnitTemplate`: one catalog row (a unit blueprint).
- `model.Group` / `model.Army`: squad and army membership.
- `service.UnitFactory`: turns a `UnitTemplate` into the matching `Unit` subclass (Simple
  Factory) and validates the whole catalog at startup.
- `service.UnitCatalogRepository`: interface, "where the templates come from".
- `service.UnitCatalogService`: loads the templates from a repository, checks them, and
  groups them by category for the menu.
- `service.ArmyBuilderService`: budget validation when adding groups and units.
- `service.CombatService`: battle resolution (the core business logic).
- `service.BattleResult`: winner (if any), number of rounds, combat log. The winner is
  stored as a `String` that is `null` for a draw; `getWinner()` returns
  `Optional.ofNullable(winner)` (Optional is used only as a return type), and
  `ArmyPrinter` reads it with `ifPresentOrElse`.
- `util.UnitCatalogLoader`: implements `UnitCatalogRepository` by reading `units_catalog.csv`.
- `util.ConsoleInputReader` / `util.ArmyPrinter`: console I/O, kept out of the domain model.
- `Application`: entry point; `main` wires the objects together and the menu loop delegates
  every rule to the services.

## Technical decisions

1. **Stats live in the catalog file, not in Java code.** `InfantryType`/`VehicleType` are
   pure classification enums; attack/defense/health/cost come from `UnitTemplate` rows
   loaded at startup. This was the only way to make "load data from a file" a real
   requirement rather than a cosmetic one, otherwise the file would just mirror constants
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
5. **The catalog is validated at startup.** `UnitCatalogService` asks
   `UnitFactory.checkCatalog()` to build every row once as a dry run, so an invalid row
   (unknown category/subtype, negative stats...) stops the application with a clear
   message at startup instead of failing in the middle of a game. The value rules
   themselves live in the model constructors, so they are written only once. The CSV
   `category` column is converted to the `UnitCategory` enum as soon as the file is read,
   so an unknown category is rejected with its line number and the rest of the code never
   handles a misspelled string.

## Where inheritance is used

`Infantry` and `Vehicle` both extend the abstract class `model.Unit`, which holds the state
shared by every unit (name, cost, base attack/defense, health) and its validation rules,
and leaves the computation of actual combat power to the subclasses.

## Where polymorphism is used

- `service.CombatService` calls `getAttackPower()` / `getDefensePower()` on whatever
  `Combatant` it is given, without knowing whether it is an `Infantry` or a `Vehicle`. Each
  subclass computes them differently:
  - **Infantry**: wounded soldiers fight less well. Below half health, attack is halved.
    Defense (equipment) does not change.
  - **Vehicle**: keeps its full firepower until destroyed, but its armor degrades. Defense
    scales with the remaining health ratio.
- `Unit.describe()` is implemented differently by each subclass and used by `ArmyPrinter`.

## Where the abstract class/interfaces are used

- `model.Unit` is an **abstract class**: it gives `Infantry`/`Vehicle` shared state and
  behavior (stat validation, health tracking, damage application) and forces them to
  define how they fight (`getAttackPower`, `getDefensePower`) and how they describe
  themselves.
- `model.Combatant` is an **interface**: it defines the combat contract (name, attack,
  defense, current health, receive damage, defeated). `CombatService` is written only
  against `Combatant`: it does not import `Unit`, `Infantry` or `Vehicle`. A future combat
  participant that is not a roster `Unit` (e.g. a fortification) could implement it without
  inheriting the cost and army-management state of `Unit`.
- `service.UnitCatalogRepository` is an **interface**: the services depend on it instead of
  on the CSV reader. `UnitCatalogLoader` is the real implementation; the tests pass a lambda
  that returns a fixed list, so they don't need a file.

## Custom exceptions

- `exception.ArmySimulationException`, base checked exception for all domain errors.
- `exception.InsufficientPointsException`, adding a group/unit would exceed the army's
  point budget.
- `exception.EmptyGroupException`, a battle was requested with a group that has no living
  units.
- `exception.InvalidUnitDataException`, the catalog file is missing, empty, or has a
  malformed or invalid row.

Standard exceptions are also used: `IOException` (reading the catalog file),
`NumberFormatException` (console input and CSV parsing), `IllegalArgumentException`
(invalid values passed to model constructors, e.g. a non-positive budget or negative
damage), `NullPointerException` via `Objects.requireNonNull`, and
`IndexOutOfBoundsException` (invalid group/unit indices).

## How the application loads its data

At startup, `main` creates a `UnitCatalogLoader` for `units_catalog.csv` (bundled as a
classpath resource under `src/main/resources`) and gives it to `UnitCatalogService` as a
`UnitCatalogRepository`. The loader checks the file's shape (column count, known category,
numeric values) and produces a list of `UnitTemplate`s. `UnitCatalogService` then asks
`UnitFactory` to build every row once to make sure each one is a valid unit, and groups the
rows by category for the menu. Every unit you add to an army is built from one of these
templates by `UnitFactory`; there is no hardcoded `new Infantry(...)` roster.

## How SOLID principles were applied

- **SRP**: model classes (`Unit`, `Group`, `Army`) hold state and their own invariants;
  `UnitFactory` only creates units; `ArmyBuilderService` only checks the budget;
  `CombatService` only resolves battles; `UnitCatalogLoader`, `ConsoleInputReader`,
  `ArmyPrinter` handle I/O; `Application` only wires objects and runs the menu.
- **OCP**: `CombatService` works with any `Combatant` and `ArmyPrinter` with any `Unit`
  subclass, so adding a new kind of unit does not change them. The choice of the subclass
  is isolated in `UnitFactory` (Simple Factory pattern): adding a kind means a new
  `UnitCategory` constant and a new `case` in the factory, and nothing else. The `switch`
  is on an enum and covers all its constants, so the compiler reports a missing case.
- **LSP**: `Infantry` and `Vehicle` are interchangeable wherever a `Unit`/`Combatant` is
  expected; they respect the same contract (non-negative stats, health between 0 and max)
  and the combat loop never checks the concrete type.
- **ISP**: `Combatant` only exposes combat-related methods, not the army-management ones
  (cost, description). `UnitCatalogRepository` has a single method.
- **DIP**: `UnitCatalogService` depends on the `UnitCatalogRepository` abstraction, not on
  the CSV reader; `CombatService` depends on `Combatant`, not on `Infantry`/`Vehicle`.
  `Application` and the services receive their collaborators through their constructors;
  all concrete classes are created in one place (`main`).

## Known limitations and rules worth knowing

- **Battle damage is permanent.** After a battle, units stay wounded or defeated, and their
  cost still counts in the army's budget. This is intentional: points measure what the army
  spent on recruitment, not its current strength, and a group that lost a fight stays
  weakened. A defeated unit can be removed from its group to free its points; a group with
  no living units cannot fight again (`EmptyGroupException`).
- **Transport capacity is information only.** The `capacity` column is loaded, validated
  (not negative) and displayed for transport vehicles, but no rule uses it yet: there is no
  "embark infantry in a vehicle" feature.
- **One army at a time** (see Purpose).

## Data file

`src/main/resources/units_catalog.csv`, columns: `category,subtype,name,cost,attack,defense,health,capacity`.
`category` is `INFANTRY` or `VEHICLE`; `subtype` is `SOLDIER`/`HEAVY`/`SPECIAL`/`LEADER` for
infantry and `TRANSPORT`/`COMBAT` for vehicles (case-insensitive).
