package com.armysimulator.model;

// Something that can fight. CombatService only knows this interface.
public interface Combatant {

    String getName();

    int getAttackPower();

    int getDefensePower();

    int getCurrentHealth();

    void receiveDamage(int amount);

    boolean isDefeated();
}
