package com.armysimulator.service;

import com.armysimulator.exception.EmptyGroupException;
import com.armysimulator.model.Group;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {

    private CombatService service;
    private Group attackers;
    private Group defenders;

    @BeforeEach
    void setUp() {
        service = new CombatService();
        attackers = new Group("Attackers");
        defenders = new Group("Defenders");
    }

    @Test
    void strongerGroupShouldWinInOneRound() throws EmptyGroupException {
        // Arrange
        attackers.addUnit(new Infantry("Striker", 10, InfantryType.SPECIAL, 50, 0, 40));
        defenders.addUnit(new Infantry("Weakling", 5, InfantryType.SOLDIER, 1, 0, 10));

        // Act
        BattleResult result = service.fight(attackers, defenders);

        // Assert
        assertEquals(Optional.of("Attackers"), result.getWinner());
        assertEquals(1, result.getRounds());
    }

    @Test
    void perfectlyMatchedGroupsShouldDrawAfterRoundCap() throws EmptyGroupException {
        // Arrange
        attackers.addUnit(new Infantry("TankA", 50, InfantryType.HEAVY, 1, 100, 1000));
        defenders.addUnit(new Infantry("TankB", 50, InfantryType.HEAVY, 1, 100, 1000));

        // Act
        BattleResult result = service.fight(attackers, defenders);

        // Assert
        assertTrue(result.getWinner().isEmpty());
        assertEquals(50, result.getRounds());
    }

    @Test
    void shouldThrowExceptionWhenAGroupHasNoUnits() {
        // Arrange
        attackers.addUnit(new Infantry("Striker", 10, InfantryType.SPECIAL, 50, 0, 40));

        // Act
        EmptyGroupException e = assertThrows(EmptyGroupException.class, () -> service.fight(attackers, defenders));

        // Assert
        assertEquals("Group \"Defenders\" has no living units and cannot take part in a battle.", e.getMessage());
    }
}