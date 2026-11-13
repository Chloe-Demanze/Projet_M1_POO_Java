package com.armysimulator.service;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.InfantryType;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.model.Vehicle;
import com.armysimulator.model.VehicleType;

import java.util.List;

// Simple Factory: the only place that knows which Unit subclass matches a catalog row.
// Adding a unit kind = a new UnitCategory constant + a new case here. The switch covers
// every constant of the enum, so the compiler reports a missing case.
public class UnitFactory {

    public Unit create(UnitTemplate template) throws InvalidUnitDataException {
        try {
            return switch (template.getCategory()) {
                case INFANTRY -> new Infantry(template.getName(), template.getCost(),
                        InfantryType.valueOf(template.getSubtype()),
                        template.getAttack(), template.getDefense(), template.getHealth());
                case VEHICLE -> new Vehicle(template.getName(), template.getCost(),
                        VehicleType.valueOf(template.getSubtype()), template.getAttack(),
                        template.getDefense(), template.getHealth(), template.getCapacity());
            };
        } catch (IllegalArgumentException e) {
            // unknown subtype or invalid stats
            throw new InvalidUnitDataException("unit \"" + template.getName() + "\": " + e.getMessage(), e);
        }
    }

    // we create each unit once to check the file is ok before starting
    public void checkCatalog(List<UnitTemplate> templates) throws InvalidUnitDataException {
        for (UnitTemplate template : templates) {
            create(template);
        }
    }
}
