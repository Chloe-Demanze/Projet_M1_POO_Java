package com.armysimulator.model;

// Something that can fight
public interface Combatant {

    int getAttackPower();

    int getDefensePower();

    void receiveDamage(int amount);

    boolean isDefeated();
}
