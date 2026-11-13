package com.armysimulator.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitTest {

    private static Infantry soldier() {
        return new Infantry("Grunt", 10, InfantryType.SOLDIER, 9, 4, 40);
    }

    private static Vehicle tank() {
        return new Vehicle("Battle Tank", 60, VehicleType.COMBAT, 20, 16, 100, 0);
    }

    @Test
    void woundedInfantryShouldDealHalfDamage() {
        Infantry grunt = soldier();

        grunt.receiveDamage(21); // under half

        assertEquals(5, grunt.getAttackPower());
        assertEquals(4, grunt.getDefensePower());
    }

    @Test
    void infantryAtExactlyHalfHealthShouldKeepFullAttack() {
        Infantry grunt = soldier();

        grunt.receiveDamage(20); // exactly half

        assertEquals(9, grunt.getAttackPower());
    }

    @Test
    void damagedVehicleShouldLoseArmorButKeepFirepower() {
        Vehicle tank = tank();

        tank.receiveDamage(50);

        assertEquals(20, tank.getAttackPower());
        assertEquals(8, tank.getDefensePower());
    }

    @Test
    void sameDamageShouldAffectEachUnitTypeDifferently() {
        // same damage, different result depending on the type
        Infantry grunt = new Infantry("Grunt", 10, InfantryType.SOLDIER, 10, 10, 100);
        Vehicle tank = new Vehicle("Tank", 10, VehicleType.COMBAT, 10, 10, 100, 0);
        List<Combatant> combatants = List.of(grunt, tank);

        combatants.forEach(c -> c.receiveDamage(60));

        assertEquals(5, grunt.getAttackPower());
        assertEquals(10, grunt.getDefensePower());
        assertEquals(10, tank.getAttackPower());
        assertEquals(4, tank.getDefensePower());
    }

    @Test
    void healthShouldNeverDropBelowZero() {
        Infantry grunt = soldier();

        grunt.receiveDamage(1000);

        assertEquals(0, grunt.getCurrentHealth());
        assertTrue(grunt.isDefeated());
    }

    @Test
    void shouldRejectNegativeDamage() {
        Infantry grunt = soldier();

        assertThrows(IllegalArgumentException.class, () -> grunt.receiveDamage(-5));
        assertEquals(40, grunt.getCurrentHealth());
    }

    @Test
    void shouldRejectInvalidUnitStats() {
        assertThrows(IllegalArgumentException.class,
                () -> new Infantry("Grunt", 0, InfantryType.SOLDIER, 9, 4, 40));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("Truck", 30, VehicleType.TRANSPORT, 1, 8, 70, -1));
    }

    @Test
    void shouldRejectArmyWithNonPositiveBudget() {
        assertThrows(IllegalArgumentException.class, () -> new Army("Empty", Faction.NEUTRAL, 0));
    }
}
