package com.armysimulator.model;

import java.util.Objects;

// Infantry: attack is divided by 2 when under half health
public class Infantry extends Unit {

    private final InfantryType type;

    public Infantry(String name, int cost, InfantryType type, int attack, int defense, int maxHealth) {
        super(name, cost, attack, defense, maxHealth);
        this.type = Objects.requireNonNull(type, "Infantry type must not be null.");
    }

    @Override
    public int getAttackPower() {
        if (isBadlyWounded()) {
            return (getBaseAttack() + 1) / 2; // rounded up
        }
        return getBaseAttack();
    }

    @Override
    public int getDefensePower() {
        return getBaseDefense();
    }

    public boolean isBadlyWounded() {
        return getCurrentHealth() * 2 < getMaxHealth();
    }

    public InfantryType getType() {
        return type;
    }

    @Override
    public String describe() {
        return "Infantry: " + type + " - " + getName() + " (" + getCost() + " pts, "
                + getCurrentHealth() + "/" + getMaxHealth() + " HP, ATK " + getAttackPower()
                + "/DEF " + getDefensePower() + status() + ")";
    }

    private String status() {
        if (isDefeated()) {
            return ", defeated";
        }
        return isBadlyWounded() ? ", wounded" : "";
    }
}
