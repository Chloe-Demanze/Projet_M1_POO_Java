package com.armysimulator.model;

// Common part of all units. Subclasses decide attack and defense.
public abstract class Unit implements Combatant {

    private final String name;
    private final int cost;
    private final int baseAttack;
    private final int baseDefense;
    private final int maxHealth;
    private int currentHealth;

    protected Unit(String name, int cost, int baseAttack, int baseDefense, int maxHealth) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unit name must not be blank.");
        }
        if (cost <= 0) {
            throw new IllegalArgumentException("Unit cost must be positive, got " + cost + ".");
        }
        if (baseAttack < 0 || baseDefense < 0) {
            throw new IllegalArgumentException("Attack and defense must not be negative.");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Unit health must be positive, got " + maxHealth + ".");
        }
        this.name = name;
        this.cost = cost;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public abstract String describe();

    @Override
    public void receiveDamage(int amount) {
        if (amount < 0) {
            // negative damage would heal the unit
            throw new IllegalArgumentException("Damage cannot be negative, got " + amount + ".");
        }
        currentHealth = Math.max(0, currentHealth - amount);
    }

    @Override
    public boolean isDefeated() {
        return currentHealth <= 0;
    }

    protected int getBaseAttack() {
        return baseAttack;
    }

    protected int getBaseDefense() {
        return baseDefense;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
}
