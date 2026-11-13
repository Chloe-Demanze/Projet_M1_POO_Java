package com.armysimulator.model;

import java.util.Objects;

// Vehicle: defense goes down when it loses health
public class Vehicle extends Unit {

    private final VehicleType type;
    private final int transportCapacity;

    public Vehicle(String name, int cost, VehicleType type, int attack, int defense, int maxHealth, int transportCapacity) {
        super(name, cost, attack, defense, maxHealth);
        if (transportCapacity < 0) {
            throw new IllegalArgumentException("Transport capacity must not be negative, got " + transportCapacity + ".");
        }
        this.type = Objects.requireNonNull(type, "Vehicle type must not be null.");
        this.transportCapacity = transportCapacity;
    }

    @Override
    public int getAttackPower() {
        return getBaseAttack();
    }

    @Override
    public int getDefensePower() {
        return getBaseDefense() * getCurrentHealth() / getMaxHealth();
    }

    public VehicleType getType() {
        return type;
    }

    public boolean isTransport() {
        return type == VehicleType.TRANSPORT;
    }

    public int getTransportCapacity() {
        return transportCapacity;
    }

    @Override
    public String describe() {
        String stats = getCost() + " pts, " + getCurrentHealth() + "/" + getMaxHealth() + " HP, ATK "
                + getAttackPower() + "/DEF " + getDefensePower();
        if (isTransport()) {
            return "Vehicle: Transport - " + getName() + " (" + stats + ", capacity " + transportCapacity + ")";
        }
        return "Vehicle: Combat - " + getName() + " (" + stats + ")";
    }
}
