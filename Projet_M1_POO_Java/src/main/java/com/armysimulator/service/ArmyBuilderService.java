package com.armysimulator.service;

import com.armysimulator.exception.InsufficientPointsException;
import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.Army;
import com.armysimulator.model.Group;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.model.Vehicle;
import com.armysimulator.model.VehicleType;

import java.util.List;

// Adds groups and units to an army while checking the points budget
public class ArmyBuilderService {

    public void addGroup(Army army, Group group) throws InsufficientPointsException {
        int cost = group.getTotalPoints();
        if (cost > army.getRemainingPoints()) {
            throw new InsufficientPointsException(cost, army.getRemainingPoints());
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
            throw new InsufficientPointsException(template.getCost(), army.getRemainingPoints());
        }
        Unit unit = createUnit(template);
        group.addUnit(unit);
        return unit;
    }

    // we create each unit once to check the file is ok before starting
    public void checkCatalog(List<UnitTemplate> templates) throws InvalidUnitDataException {
        for (UnitTemplate template : templates) {
            createUnit(template);
        }
    }

    private Unit createUnit(UnitTemplate template) throws InvalidUnitDataException {
        try {
            if (template.getCategory().equals("INFANTRY")) {
                InfantryType type = InfantryType.valueOf(template.getSubtype());
                return new Infantry(template.getName(), template.getCost(), type,
                        template.getAttack(), template.getDefense(), template.getHealth());
            } else if (template.getCategory().equals("VEHICLE")) {
                VehicleType type = VehicleType.valueOf(template.getSubtype());
                return new Vehicle(template.getName(), template.getCost(), type, template.getAttack(),
                        template.getDefense(), template.getHealth(), template.getCapacity());
            }
        } catch (IllegalArgumentException e) {
            // unknown subtype or invalid stats
            throw new InvalidUnitDataException("unit \"" + template.getName() + "\": " + e.getMessage(), e);
        }
        throw new InvalidUnitDataException("unknown unit category \"" + template.getCategory()
                + "\" for unit \"" + template.getName() + "\".");
    }
}
