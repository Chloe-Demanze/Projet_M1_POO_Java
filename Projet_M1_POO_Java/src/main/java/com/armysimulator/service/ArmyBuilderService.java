package com.armysimulator.service;

import com.armysimulator.exception.InsufficientPointsException;
import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.Army;
import com.armysimulator.model.Group;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitTemplate;

import java.util.Objects;

// Adds groups and units to an army while checking the points budget.
// Building the unit itself is delegated to UnitFactory.
public class ArmyBuilderService {

    private final UnitFactory unitFactory;

    public ArmyBuilderService(UnitFactory unitFactory) {
        this.unitFactory = Objects.requireNonNull(unitFactory, "Unit factory must not be null.");
    }

    public void addGroup(Army army, Group group) throws InsufficientPointsException {
        int cost = group.getTotalPoints();
        if (cost > army.getRemainingPoints()) {
            throw new InsufficientPointsException("group \"" + group.getName() + "\"", cost, army.getRemainingPoints());
        }
        army.addGroup(group);
    }

    public Unit addUnitToGroup(Army army, Group group, UnitTemplate template)
            throws InsufficientPointsException, InvalidUnitDataException {
        // otherwise the points wouldn't be counted in the army
        if (!army.getGroups().contains(group)) {
            throw new IllegalArgumentException("Group \"" + group.getName() + "\" is not part of army \"" + army.getName() + "\".");
        }
        if (template.getCost() > army.getRemainingPoints()) {
            throw new InsufficientPointsException("unit \"" + template.getName() + "\"", template.getCost(),
                    army.getRemainingPoints());
        }
        Unit unit = unitFactory.create(template);
        group.addUnit(unit);
        return unit;
    }
}
