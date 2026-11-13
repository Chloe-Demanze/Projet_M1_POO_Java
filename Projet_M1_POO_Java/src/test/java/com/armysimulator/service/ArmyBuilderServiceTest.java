package com.armysimulator.service;

import com.armysimulator.exception.ArmySimulationException;
import com.armysimulator.exception.InsufficientPointsException;
import com.armysimulator.model.Army;
import com.armysimulator.model.Faction;
import com.armysimulator.model.Group;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmyBuilderServiceTest {

    private ArmyBuilderService service;
    private Group group;

    @BeforeEach
    void setUp() {
        service = new ArmyBuilderService(new UnitFactory());
        group = new Group("Squad A");
    }

    private static UnitTemplate infantryTemplate(int cost) {
        return new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Grunt", cost, 8, 4, 30, 0);
    }

    private Army armyWithGroup(int maxPoints) {
        Army army = new Army("Test Army", Faction.ALLIANCE, maxPoints);
        army.addGroup(group);
        return army;
    }

    @Test
    void shouldAddUnitWhenWithinBudget() throws ArmySimulationException {
        // Arrange
        Army army = armyWithGroup(100);

        // Act
        Unit unit = service.addUnitToGroup(army, group, infantryTemplate(40));

        // Assert
        assertEquals(40, army.getTotalPoints());
        assertEquals(1, group.getUnits().size());
        assertEquals(unit, group.getUnits().get(0));
    }

    @Test
    void shouldAllowUnitThatExactlyMatchesRemainingBudget() throws ArmySimulationException {
        // Arrange
        Army army = armyWithGroup(50);

        // Act
        service.addUnitToGroup(army, group, infantryTemplate(50));

        // Assert
        assertEquals(0, army.getRemainingPoints());
    }

    @Test
    void shouldThrowExceptionWhenBudgetIsInsufficient() {
        // Arrange
        Army army = armyWithGroup(20);

        // Act
        InsufficientPointsException e = assertThrows(InsufficientPointsException.class,
                () -> service.addUnitToGroup(army, group, infantryTemplate(25)));

        // Assert
        assertEquals("Cannot add a unit costing 25 points: only 20 points remaining.", e.getMessage());
        assertTrue(group.getUnits().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAddingAGroupThatExceedsTheBudget() {
        // Arrange
        Army army = new Army("Test Army", Faction.HORDE, 50);
        Group veterans = new Group("Veterans");
        veterans.addUnit(new Infantry("Warchief", 60, InfantryType.LEADER, 12, 10, 50));

        // Act
        InsufficientPointsException e = assertThrows(InsufficientPointsException.class,
                () -> service.addGroup(army, veterans));

        // Assert
        assertEquals("Cannot add a unit costing 60 points: only 50 points remaining.", e.getMessage());
        assertTrue(army.getGroups().isEmpty());
    }

    @Test
    void shouldRejectAGroupThatDoesNotBelongToTheArmy() {
        // Arrange
        Army army = new Army("Test Army", Faction.ALLIANCE, 100);
        Group outsider = new Group("Outsiders");

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service.addUnitToGroup(army, outsider, infantryTemplate(10)));

        // Assert
        assertEquals("Group \"Outsiders\" is not part of army \"Test Army\".", e.getMessage());
    }
}
