package com.armysimulator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitTest {

    private Infantry grunt;
    private Vehicle tank;

    @BeforeEach
    void setUp() {
        grunt = new Infantry("Grunt", 10, InfantryType.SOLDIER, 9, 4, 40);
        tank = new Vehicle("Battle Tank", 60, VehicleType.COMBAT, 20, 16, 100, 0);
    }

    @Test
    void woundedInfantryShouldDealHalfDamage() {
        // Arrange: grunt has 40 HP

        // Act
        grunt.receiveDamage(21); // under half

        // Assert
        assertEquals(5, grunt.getAttackPower());
        assertEquals(4, grunt.getDefensePower());
    }

    @Test
    void infantryAtExactlyHalfHealthShouldKeepFullAttack() {
        // Arrange: grunt has 40 HP

        // Act
        grunt.receiveDamage(20); // exactly half

        // Assert
        assertEquals(9, grunt.getAttackPower());
    }

    @Test
    void damagedVehicleShouldLoseArmorButKeepFirepower() {
        // Arrange: tank has 100 HP

        // Act
        tank.receiveDamage(50);

        // Assert
        assertEquals(20, tank.getAttackPower());
        assertEquals(8, tank.getDefensePower());
    }

    @Test
    void sameDamageShouldAffectEachUnitTypeDifferently() {
        // Arrange: same stats, different type
        Infantry infantry = new Infantry("Grunt", 10, InfantryType.SOLDIER, 10, 10, 100);
        Vehicle vehicle = new Vehicle("Tank", 10, VehicleType.COMBAT, 10, 10, 100, 0);
        List<Combatant> combatants = List.of(infantry, vehicle);

        // Act
        combatants.forEach(c -> c.receiveDamage(60));

        // Assert
        assertEquals(5, infantry.getAttackPower());
        assertEquals(10, infantry.getDefensePower());
        assertEquals(10, vehicle.getAttackPower());
        assertEquals(4, vehicle.getDefensePower());
    }

    @Test
    void healthShouldNeverDropBelowZero() {
        // Arrange: grunt has 40 HP

        // Act
        grunt.receiveDamage(1000);

        // Assert
        assertEquals(0, grunt.getCurrentHealth());
        assertTrue(grunt.isDefeated());
    }

    @Test
    void shouldRejectNegativeDamage() {
        // Arrange: grunt has 40 HP

        // Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> grunt.receiveDamage(-5));

        // Assert
        assertEquals("Damage cannot be negative, got -5.", e.getMessage());
        assertEquals(40, grunt.getCurrentHealth());
    }

    @Test
    void shouldRejectUnitWithNonPositiveCost() {
        // Arrange + Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Infantry("Grunt", 0, InfantryType.SOLDIER, 9, 4, 40));

        // Assert
        assertEquals("Unit cost must be positive, got 0.", e.getMessage());
    }

    @Test
    void shouldRejectVehicleWithNegativeCapacity() {
        // Arrange + Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("Truck", 30, VehicleType.TRANSPORT, 1, 8, 70, -1));

        // Assert
        assertEquals("Transport capacity must not be negative, got -1.", e.getMessage());
    }

    @Test
    void shouldRejectArmyWithNonPositiveBudget() {
        // Arrange + Act
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Army("Empty", Faction.NEUTRAL, 0));

        // Assert
        assertEquals("Army point budget must be positive, got 0.", e.getMessage());
    }
}
