package com.armysimulator.service;

import com.armysimulator.exception.EmptyGroupException;
import com.armysimulator.model.Group;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {

    private final CombatService service = new CombatService();

    @Test
    void strongerGroupShouldWinDeterministically() throws EmptyGroupException {
        Group attackers = new Group("Attackers");
        attackers.addUnit(new Infantry("Striker", 10, InfantryType.SPECIAL, 50, 0, 40));

        Group defenders = new Group("Defenders");
        defenders.addUnit(new Infantry("Weakling", 5, InfantryType.SOLDIER, 1, 0, 10));

        BattleResult result = service.fight(attackers, defenders);

        assertTrue(result.getWinner().isPresent());
        assertEquals("Attackers", result.getWinner().get());
        assertEquals(1, result.getRounds());
    }

    @Test
    void perfectlyMatchedGroupsShouldDrawAfterRoundCap() throws EmptyGroupException {
        Group attackers = new Group("Attackers");
        attackers.addUnit(new Infantry("TankA", 50, InfantryType.HEAVY, 1, 100, 1000));

        Group defenders = new Group("Defenders");
        defenders.addUnit(new Infantry("TankB", 50, InfantryType.HEAVY, 1, 100, 1000));

        BattleResult result = service.fight(attackers, defenders);

        assertTrue(result.getWinner().isEmpty());
        assertEquals(50, result.getRounds());
    }

    @Test
    void shouldThrowExceptionWhenAGroupHasNoUnits() {
        Group attackers = new Group("Attackers");
        attackers.addUnit(new Infantry("Striker", 10, InfantryType.SPECIAL, 50, 0, 40));

        Group emptyDefenders = new Group("Defenders");

        assertThrows(EmptyGroupException.class, () -> service.fight(attackers, emptyDefenders));
    }
}
