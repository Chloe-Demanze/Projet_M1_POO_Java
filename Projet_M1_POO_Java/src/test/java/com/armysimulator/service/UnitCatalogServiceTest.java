package com.armysimulator.service;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// The repository is replaced by a lambda: no csv file needed, thanks to the UnitCatalogRepository interface.
class UnitCatalogServiceTest {

    private UnitFactory factory;

    @BeforeEach
    void setUp() {
        factory = new UnitFactory();
    }

    @Test
    void shouldGroupTemplatesByCategory() throws IOException, InvalidUnitDataException {
        // Arrange
        UnitCatalogRepository repository = () -> List.of(
                new UnitTemplate(UnitCategory.VEHICLE, "COMBAT", "Battle Tank", 60, 20, 16, 100, 0),
                new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Grunt", 10, 8, 4, 30, 0),
                new UnitTemplate(UnitCategory.INFANTRY, "HEAVY", "Juggernaut", 25, 10, 14, 60, 0));

        // Act
        UnitCatalogService catalog = new UnitCatalogService(repository, factory);

        // Assert
        assertEquals(List.of(UnitCategory.INFANTRY, UnitCategory.VEHICLE), catalog.getCategories());
        assertEquals(2, catalog.getTemplates(UnitCategory.INFANTRY).size());
        assertEquals(1, catalog.getTemplates(UnitCategory.VEHICLE).size());
    }

    @Test
    void shouldReturnNoTemplateForACategoryAbsentFromTheCatalog() throws IOException, InvalidUnitDataException {
        // Arrange
        UnitCatalogRepository repository = () -> List.of(
                new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Grunt", 10, 8, 4, 30, 0));

        // Act
        UnitCatalogService catalog = new UnitCatalogService(repository, factory);

        // Assert
        assertTrue(catalog.getTemplates(UnitCategory.VEHICLE).isEmpty());
    }

    @Test
    void shouldRefuseToStartWithAnInvalidRow() {
        // Arrange
        UnitCatalogRepository repository = () -> List.of(
                new UnitTemplate(UnitCategory.INFANTRY, "SOLDIER", "Ghost", 10, 8, 4, 0, 0));

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> new UnitCatalogService(repository, factory));

        // Assert
        assertTrue(e.getMessage().contains("\"Ghost\""));
    }
}
