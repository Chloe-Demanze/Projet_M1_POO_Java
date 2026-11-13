package com.armysimulator.service;

import com.armysimulator.exception.EmptyGroupException;
import com.armysimulator.model.Group;
import com.armysimulator.model.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Battle between two groups, round by round
public class CombatService {

    private static final int MAX_ROUNDS = 50;

    public BattleResult fight(Group attackers, Group defenders) throws EmptyGroupException {
        if (!attackers.hasLivingUnits()) {
            throw new EmptyGroupException(attackers.getName());
        }
        if (!defenders.hasLivingUnits()) {
            throw new EmptyGroupException(defenders.getName());
        }

        List<String> log = new ArrayList<>();
        int round = 0;

        while (round < MAX_ROUNDS && attackers.hasLivingUnits() && defenders.hasLivingUnits()) {
            round++;
            log.add("--- Round " + round + " ---");
            strike(attackers, defenders, log);
            if (!defenders.hasLivingUnits()) {
                break;
            }
            strike(defenders, attackers, log);
        }

        String winner = null; // stays null for a draw
        if (attackers.hasLivingUnits() && !defenders.hasLivingUnits()) {
            winner = attackers.getName();
        } else if (defenders.hasLivingUnits() && !attackers.hasLivingUnits()) {
            winner = defenders.getName();
        }

        return new BattleResult(winner, round, log);
    }

    private void strike(Group side, Group opposingSide, List<String> log) {
        for (Unit attacker : side.getLivingUnits()) {
            Optional<Unit> target = opposingSide.getLivingUnits().stream().findFirst();
            if (target.isEmpty()) {
                return;
            }
            Unit defender = target.get();
            int damage = Math.max(1, attacker.getAttackPower() - defender.getDefensePower() / 2);
            defender.receiveDamage(damage);
            log.add(attacker.getName() + " hits " + defender.getName() + " for " + damage + " damage"
                    + (defender.isDefeated() ? " (defeated)" : " (" + defender.getCurrentHealth() + " HP left)"));
        }
    }
}
