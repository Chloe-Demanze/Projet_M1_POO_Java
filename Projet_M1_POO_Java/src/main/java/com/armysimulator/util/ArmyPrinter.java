package com.armysimulator.util;

import com.armysimulator.model.Army;
import com.armysimulator.model.Group;
import com.armysimulator.model.Unit;
import com.armysimulator.service.BattleResult;

// Display of the army and battle results (with colors)
public class ArmyPrinter {

    private static final String RESET = "[0m";
    private static final String GREEN = "[32m";
    private static final String RED = "[31m";
    private static final String YELLOW = "[33m";
    private static final String BLUE = "[34m";

    public void printArmy(Army army) {
        System.out.println(BLUE + "===================================" + RESET);
        System.out.println(BLUE + "       Army: " + YELLOW + army.getName() + RESET);
        System.out.println(BLUE + "===================================" + RESET);
        System.out.println("Faction      : " + YELLOW + army.getFaction() + RESET);
        System.out.println("Max points   : " + YELLOW + army.getMaxPoints() + RESET);
        System.out.println("Points used  : " + (army.getTotalPoints() > army.getMaxPoints() ? RED : GREEN)
                + army.getTotalPoints() + RESET);
        System.out.println(BLUE + "-----------------------------------" + RESET);
        System.out.println("Groups:");

        if (army.getGroups().isEmpty()) {
            System.out.println("No groups in the army.");
            return;
        }

        for (Group group : army.getGroups()) {
            System.out.println("- " + RED + group.getName() + RESET);
            for (Unit unit : group.getUnits()) {
                System.out.println(YELLOW + "  " + unit.describe() + RESET);
            }
        }
    }

    public void printBattleResult(BattleResult result) {
        System.out.println(BLUE + "--------- Battle report ---------" + RESET);
        result.getLog().forEach(System.out::println);
        System.out.println(BLUE + "----------------------------------" + RESET);
        if (result.getWinner().isPresent()) {
            System.out.println(GREEN + "Winner: " + result.getWinner().get() + RESET + " after " + result.getRounds() + " round(s).");
        } else {
            System.out.println(YELLOW + "Draw" + RESET + " after " + result.getRounds() + " round(s).");
        }
    }
}
