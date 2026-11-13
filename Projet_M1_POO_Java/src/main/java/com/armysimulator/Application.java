package com.armysimulator;

import com.armysimulator.exception.ArmySimulationException;
import com.armysimulator.model.Army;
import com.armysimulator.model.Faction;
import com.armysimulator.model.Group;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.service.ArmyBuilderService;
import com.armysimulator.service.BattleResult;
import com.armysimulator.service.CombatService;
import com.armysimulator.service.UnitCatalogService;
import com.armysimulator.util.ArmyPrinter;
import com.armysimulator.util.ConsoleInputReader;
import com.armysimulator.util.UnitCatalogLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

// Creates the objects and runs the menu. The rules are in the services.
public class Application {

    private static final String CATALOG_RESOURCE = "/units_catalog.csv";

    private final ConsoleInputReader input;
    private final ArmyPrinter printer;
    private final UnitCatalogService catalogService;
    private final ArmyBuilderService armyBuilderService;
    private final CombatService combatService;

    private Army army;

    public Application(ConsoleInputReader input, ArmyPrinter printer, UnitCatalogService catalogService,
                       ArmyBuilderService armyBuilderService, CombatService combatService) {
        this.input = input;
        this.printer = printer;
        this.catalogService = catalogService;
        this.armyBuilderService = armyBuilderService;
        this.combatService = combatService;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (InputStream catalogStream = Application.class.getResourceAsStream(CATALOG_RESOURCE)) {
            if (catalogStream == null) {
                System.out.println("Fatal error: catalog file " + CATALOG_RESOURCE + " not found on classpath.");
                return;
            }
            List<UnitTemplate> templates = new UnitCatalogLoader().load(catalogStream);
            ArmyBuilderService armyBuilderService = new ArmyBuilderService();
            armyBuilderService.checkCatalog(templates);

            new Application(new ConsoleInputReader(scanner), new ArmyPrinter(), new UnitCatalogService(templates),
                    armyBuilderService, new CombatService()).run();
        } catch (IOException | ArmySimulationException e) {
            System.out.println("Fatal error while loading the unit catalog: " + e.getMessage());
        }
    }

    private void run() {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create an army");
            System.out.println("2. Add a group");
            System.out.println("3. Add a unit");
            System.out.println("4. Display the army");
            System.out.println("5. Simulate a battle between two groups");
            System.out.println("6. Remove a group or a unit");
            System.out.println("7. Quit");

            int choice = input.readInRange("Choice: ", 1, 7);
            try {
                switch (choice) {
                    case 1 -> createArmy();
                    case 2 -> addGroup();
                    case 3 -> addUnit();
                    case 4 -> displayArmy();
                    case 5 -> simulateBattle();
                    case 6 -> removeGroupOrUnit();
                    case 7 -> {
                        return;
                    }
                }
            } catch (ArmySimulationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void createArmy() {
        // the application manages one army at a time: ask before throwing the current one away
        if (army != null && !input.confirm("Army \"" + army.getName() + "\" will be replaced and lost. Continue?")) {
            return;
        }
        String armyName = input.readNonEmptyLine("Enter the army's name: ");
        System.out.println("Choose a faction:");
        Faction[] factions = Faction.values();
        for (int i = 0; i < factions.length; i++) {
            System.out.println((i + 1) + ". " + factions[i]);
        }
        Faction faction = factions[input.readInRange("Choice: ", 1, factions.length) - 1];
        int maxPoints = input.readPositiveInt("Enter the max points (must be positive): ");
        army = new Army(armyName, faction, maxPoints);
    }

    private void addGroup() throws ArmySimulationException {
        if (!requireArmy()) {
            return;
        }
        String groupName = input.readNonEmptyLine("Enter the group's name: ");
        armyBuilderService.addGroup(army, new Group(groupName));
    }

    private void addUnit() throws ArmySimulationException {
        if (!requireArmy()) {
            return;
        }
        if (army.getGroups().isEmpty()) {
            System.out.println("Add a group before adding units.");
            return;
        }

        Group group = chooseGroup("Choose a group:");

        List<String> categories = catalogService.getCategories();
        System.out.println("Choose a unit category:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        String category = categories.get(input.readInRange("Choice: ", 1, categories.size()) - 1);
        List<UnitTemplate> templates = catalogService.getTemplates(category);

        System.out.println("Available units (" + army.getRemainingPoints() + " points remaining):");
        for (int i = 0; i < templates.size(); i++) {
            UnitTemplate t = templates.get(i);
            System.out.println((i + 1) + ". " + t.getName() + " [" + t.getSubtype() + "] - " + t.getCost() + " pts, "
                    + t.getHealth() + " HP, ATK " + t.getAttack() + "/DEF " + t.getDefense()
                    + (t.getCapacity() > 0 ? ", capacity " + t.getCapacity() : ""));
        }
        int templateIndex = input.readInRange("Choice: ", 1, templates.size());
        armyBuilderService.addUnitToGroup(army, group, templates.get(templateIndex - 1));
    }

    private void displayArmy() {
        if (!requireArmy()) {
            return;
        }
        printer.printArmy(army);
    }

    private void simulateBattle() throws ArmySimulationException {
        if (!requireArmy()) {
            return;
        }
        if (army.getGroups().size() < 2) {
            System.out.println("You need at least two groups to simulate a battle.");
            return;
        }

        Group attackers = chooseGroup("Choose the attacking group:");
        Group defenders = chooseGroup("Choose the defending group:");
        if (attackers == defenders) {
            System.out.println("A group cannot fight itself.");
            return;
        }

        BattleResult result = combatService.fight(attackers, defenders);
        printer.printBattleResult(result);
    }

    private void removeGroupOrUnit() {
        if (!requireArmy()) {
            return;
        }
        if (army.getGroups().isEmpty()) {
            System.out.println("No group to remove.");
            return;
        }
        System.out.println("1. Remove a group");
        System.out.println("2. Remove a unit");
        int removalChoice = input.readInRange("Choice: ", 1, 2);

        System.out.println(removalChoice == 1 ? "Choose a group to remove:" : "Choose the group containing the unit to remove:");
        int groupIndex = chooseGroupIndex();

        if (removalChoice == 1) {
            army.removeGroup(groupIndex);
            return;
        }

        Group group = army.getGroups().get(groupIndex);
        if (group.getUnits().isEmpty()) {
            System.out.println("This group contains no unit.");
            return;
        }
        System.out.println("Choose a unit to remove:");
        for (int i = 0; i < group.getUnits().size(); i++) {
            System.out.println((i + 1) + ". " + group.getUnits().get(i).getName());
        }
        int unitIndex = input.readInRange("Choice: ", 1, group.getUnits().size()) - 1;
        group.removeUnit(unitIndex);
    }

    private Group chooseGroup(String prompt) {
        System.out.println(prompt);
        return army.getGroups().get(chooseGroupIndex());
    }

    private int chooseGroupIndex() {
        List<Group> groups = army.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getName());
        }
        return input.readInRange("Choice: ", 1, groups.size()) - 1;
    }

    private boolean requireArmy() {
        if (army == null) {
            System.out.println("Create an army first.");
            return false;
        }
        return true;
    }
}
