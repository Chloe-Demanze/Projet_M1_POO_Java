package com.armysimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Army {

    private final String name;
    private final Faction faction;
    private final int maxPoints;
    private final List<Group> groups = new ArrayList<>();

    public Army(String name, Faction faction, int maxPoints) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Army name must not be blank.");
        }
        if (maxPoints <= 0) {
            throw new IllegalArgumentException("Army point budget must be positive, got " + maxPoints + ".");
        }
        this.name = name;
        this.faction = Objects.requireNonNull(faction, "Faction must not be null.");
        this.maxPoints = maxPoints;
    }

    public String getName() {
        return name;
    }

    public Faction getFaction() {
        return faction;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public void addGroup(Group group) {
        groups.add(Objects.requireNonNull(group, "Group must not be null."));
    }

    public void removeGroup(int index) {
        if (index < 0 || index >= groups.size()) {
            throw new IndexOutOfBoundsException("No group at index " + index + " in army \"" + name + "\".");
        }
        groups.remove(index);
    }

    public int getTotalPoints() {
        return groups.stream().mapToInt(Group::getTotalPoints).sum();
    }

    public int getRemainingPoints() {
        return maxPoints - getTotalPoints();
    }
}
