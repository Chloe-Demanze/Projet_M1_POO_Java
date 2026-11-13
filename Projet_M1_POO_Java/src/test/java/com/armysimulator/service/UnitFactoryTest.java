package com.armysimulator.service;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.Infantry;
import com.armysimulator.model.Unit;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitFactoryTest {

    private UnitFactory factory;

    @BeforeEach
    void setUp() {
        factory = new UnitFactory();
    }

    @Test
    void shouldCreateInfantryFromAnInfantryTemplate() throws InvalidUnitDataException {
        // Arrange
        UnitTemplate template = new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Grunt", 10, 8, 4, 30, 0);

        // Act
        Unit unit = factory.create(template);

        // Assert
        assertInstanceOf(Infantry.class, unit);
    }

    @Test
    void shouldCreateVehicleFromAVehicleTemplate() throws InvalidUnitDataException {
        // Arrange
        UnitTemplate template = new UnitTemplate(UnitCategory.VEHICLE, "TRANSPORT", "Troop Carrier", 35, 2, 10, 80, 6);

        // Act
        Unit unit = factory.create(template);

        // Assert
        Vehicle vehicle = assertInstanceOf(Vehicle.class, unit);
        assertTrue(vehicle.isTransport());
        assertEquals(6, vehicle.getTransportCapacity());
    }

    @Test
    void shouldAcceptAValidCatalog() {
        // Arrange
        List<UnitTemplate> templates = List.of(
                new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Grunt", 10, 8, 4, 30, 0),
                new UnitTemplate(UnitCategory.VEHICLE, "COMBAT", "Battle Tank", 60, 20, 16, 100, 0));

        // Act + Assert
        assertDoesNotThrow(() -> factory.checkCatalog(templates));
    }

    @Test
    void shouldRejectCatalogWithUnknownSubtype() {
        // Arrange
        List<UnitTemplate> templates = List.of(
                new UnitTemplate(UnitCategory.INFANTRY, "PILOT", "Ace", 10, 8, 4, 30, 0));

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> factory.checkCatalog(templates));

        // Assert
        assertTrue(e.getMessage().contains("\"Ace\""));
        assertTrue(e.getMessage().contains("PILOT"));
    }

    @Test
    void shouldRejectCatalogWithZeroHealth() {
        // Arrange
        List<UnitTemplate> templates = List.of(
                new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Ghost", 10, 8, 4, 0, 0));

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> factory.checkCatalog(templates));

        // Assert
        assertEquals("Invalid unit catalog data: unit \"Ghost\": Unit health must be positive, got 0.", e.getMessage());
    }

    @Test
    void shouldRejectCatalogWithNegativeAttack() {
        // Arrange
        List<UnitTemplate> templates = List.of(
                new UnitTemplate(UnitCategory.VEHICLE, "COMBAT", "Broken Tank", 60, -5, 16, 100, 0));

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> factory.checkCatalog(templates));

        // Assert
        assertEquals("Invalid unit catalog data: unit \"Broken Tank\": Attack and defense must not be negative.",
                e.getMessage());
    }
}
