package com.armysimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Group {

    private final String name;
    private final List<Unit> units = new ArrayList<>();

    public Group(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Group name must not be blank.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public void addUnit(Unit unit) {
        units.add(Objects.requireNonNull(unit, "Unit must not be null."));
    }

    public void removeUnit(int index) {
        if (index < 0 || index >= units.size()) {
            throw new IndexOutOfBoundsException("No unit at index " + index + " in group \"" + name + "\".");
        }
        units.remove(index);
    }

    public int getTotalPoints() {
        return units.stream().mapToInt(Unit::getCost).sum();
    }

    public boolean hasLivingUnits() {
        return units.stream().anyMatch(unit -> !unit.isDefeated());
    }

    public List<Unit> getLivingUnits() {
        return units.stream().filter(unit -> !unit.isDefeated()).toList();
    }
}
