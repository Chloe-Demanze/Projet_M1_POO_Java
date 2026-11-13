package com.armysimulator.service;

import com.armysimulator.model.UnitTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitCatalogServiceTest {

    @Test
    void shouldGroupTemplatesByCategory() {
        List<UnitTemplate> templates = List.of(
                new UnitTemplate("VEHICLE", "COMBAT", "Battle Tank", 60, 20, 16, 100, 0),
                new UnitTemplate("INFANTRY", "SOLDIER", "Grunt", 10, 8, 4, 30, 0),
                new UnitTemplate("INFANTRY", "HEAVY", "Juggernaut", 25, 10, 14, 60, 0));

        UnitCatalogService catalog = new UnitCatalogService(templates);

        assertEquals(List.of("INFANTRY", "VEHICLE"), catalog.getCategories());
        assertEquals(2, catalog.getTemplates("INFANTRY").size());
        assertEquals(1, catalog.getTemplates("VEHICLE").size());
        assertTrue(catalog.getTemplates("AIRCRAFT").isEmpty());
    }
}
