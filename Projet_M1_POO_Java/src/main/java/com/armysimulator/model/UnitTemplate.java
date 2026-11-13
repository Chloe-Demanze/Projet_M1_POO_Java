package com.armysimulator.model;

// One line of the csv file
public class UnitTemplate {

    private final String category;
    private final String subtype;
    private final String name;
    private final int cost;
    private final int attack;
    private final int defense;
    private final int health;
    private final int capacity;

    public UnitTemplate(String category, String subtype, String name, int cost,
                        int attack, int defense, int health, int capacity) {
        this.category = category;
        this.subtype = subtype;
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;
        this.health = health;
        this.capacity = capacity;
    }

    public String getCategory() {
        return category;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealth() {
        return health;
    }

    public int getCapacity() {
        return capacity;
    }
}
