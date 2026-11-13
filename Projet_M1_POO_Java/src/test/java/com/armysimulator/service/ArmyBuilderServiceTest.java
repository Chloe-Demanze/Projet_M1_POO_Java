package com.armysimulator.service;

import com.armysimulator.exception.ArmySimulationException;
import com.armysimulator.exception.InsufficientPointsException;
import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.Army;
import com.armysimulator.model.Faction;
import com.armysimulator.model.Group;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.model.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmyBuilderServiceTest {

    private final ArmyBuilderService service = new ArmyBuilderService();

    private static UnitTemplate infantryTemplate(int cost) {
        return new UnitTemplate("INFANTRY", "SOLDIER", "Grunt", cost, 8, 4, 30, 0);
    }

    private static Army armyWithGroup(int maxPoints, Group group) {
        Army army = new Army("Test Army", Faction.ALLIANCE, maxPoints);
        army.addGroup(group);
        return army;
    }

    @Test
    void shouldAddUnitWhenWithinBudget() throws ArmySimulationException {
        Group group = new Group("Squad A");
        Army army = armyWithGroup(100, group);

        Unit unit = service.addUnitToGroup(army, group, infantryTemplate(40));

        assertEquals(40, army.getTotalPoints());
        assertEquals(1, group.getUnits().size());
        assertEquals(unit, group.getUnits().get(0));
    }

    @Test
    void shouldCreateTheUnitTypeMatchingTheTemplateCategory() throws ArmySimulationException {
        Group group = new Group("Squad A");
        Army army = armyWithGroup(200, group);

        Unit infantry = service.addUnitToGroup(army, group, infantryTemplate(10));
        Unit vehicle = service.addUnitToGroup(army, group,
                new UnitTemplate("VEHICLE", "TRANSPORT", "Troop Carrier", 35, 2, 10, 80, 6));

        assertInstanceOf(Infantry.class, infantry);
        assertInstanceOf(Vehicle.class, vehicle);
        assertTrue(((Vehicle) vehicle).isTransport());
    }

    @Test
    void shouldAllowUnitThatExactlyMatchesRemainingBudget() throws ArmySimulationException {
        Group group = new Group("Squad A");
        Army army = armyWithGroup(50, group);

        service.addUnitToGroup(army, group, infantryTemplate(50));

        assertEquals(50, army.getTotalPoints());
        assertEquals(0, army.getRemainingPoints());
    }

    @Test
    void shouldThrowExceptionWhenBudgetIsInsufficient() {
        Group group = new Group("Squad A");
        Army army = armyWithGroup(20, group);

        assertThrows(InsufficientPointsException.class,
                () -> service.addUnitToGroup(army, group, infantryTemplate(25)));
        assertEquals(0, group.getUnits().size());
    }

    @Test
    void shouldThrowExceptionWhenAddingAGroupThatExceedsTheBudget() {
        Army army = new Army("Test Army", Faction.HORDE, 50);
        Group veterans = new Group("Veterans");
        veterans.addUnit(new Infantry("Warchief", 60, InfantryType.LEADER, 12, 10, 50));

        assertThrows(InsufficientPointsException.class, () -> service.addGroup(army, veterans));
        assertTrue(army.getGroups().isEmpty());
    }

    @Test
    void shouldRejectAGroupThatDoesNotBelongToTheArmy() {
        Army army = new Army("Test Army", Faction.ALLIANCE, 100);
        Group outsider = new Group("Outsiders");

        assertThrows(IllegalArgumentException.class,
                () -> service.addUnitToGroup(army, outsider, infantryTemplate(10)));
    }

    @Test
    void shouldAcceptAValidCatalog() throws InvalidUnitDataException {
        service.checkCatalog(List.of(
                infantryTemplate(10),
                new UnitTemplate("VEHICLE", "COMBAT", "Battle Tank", 60, 20, 16, 100, 0)));
    }

    @Test
    void shouldRejectCatalogWithUnknownSubtype() {
        List<UnitTemplate> templates = List.of(new UnitTemplate("INFANTRY", "PILOT", "Ace", 10, 8, 4, 30, 0));

        assertThrows(InvalidUnitDataException.class, () -> service.checkCatalog(templates));
    }

    @Test
    void shouldRejectCatalogWithUnknownCategory() {
        List<UnitTemplate> templates = List.of(new UnitTemplate("AIRCRAFT", "COMBAT", "Fighter", 50, 20, 5, 40, 0));

        assertThrows(InvalidUnitDataException.class, () -> service.checkCatalog(templates));
    }

    @Test
    void shouldRejectCatalogWithInvalidStats() {
        List<UnitTemplate> noHealth = List.of(new UnitTemplate("INFANTRY", "SOLDIER", "Ghost", 10, 8, 4, 0, 0));
        List<UnitTemplate> negativeAttack = List.of(new UnitTemplate("VEHICLE", "COMBAT", "Broken Tank", 60, -5, 16, 100, 0));

        assertThrows(InvalidUnitDataException.class, () -> service.checkCatalog(noHealth));
        assertThrows(InvalidUnitDataException.class, () -> service.checkCatalog(negativeAttack));
    }
}
