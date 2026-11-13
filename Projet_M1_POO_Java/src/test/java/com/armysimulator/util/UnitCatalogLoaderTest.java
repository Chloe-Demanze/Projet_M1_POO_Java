package com.armysimulator.util;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnitCatalogLoaderTest {

    private static final String HEADER = "category,subtype,name,cost,attack,defense,health,capacity\n";

    private static InputStream streamOf(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldParseValidCatalogRows() throws IOException, InvalidUnitDataException {
        // Arrange
        String csv = HEADER
                + "INFANTRY,SOLDIER,Grunt,10,8,4,30,0\n"
                + "VEHICLE,TRANSPORT,Troop Carrier,35,2,10,80,6\n";

        // Act
        List<UnitTemplate> templates = UnitCatalogLoader.parse(streamOf(csv));

        // Assert
        assertEquals(2, templates.size());
        UnitTemplate grunt = templates.get(0);
        assertEquals("Grunt", grunt.getName());
        assertEquals(10, grunt.getCost());
        assertEquals(30, grunt.getHealth());
        assertEquals(6, templates.get(1).getCapacity());
    }

    @Test
    void shouldNormalizeCategoryAndSubtypeToUpperCase() throws IOException, InvalidUnitDataException {
        // Arrange
        String csv = HEADER + " infantry , soldier ,Grunt,10,8,4,30,0\n";

        // Act
        UnitTemplate grunt = UnitCatalogLoader.parse(streamOf(csv)).get(0);

        // Assert
        assertEquals(UnitCategory.INFANTRY, grunt.getCategory());
        assertEquals("SOLDIER", grunt.getSubtype());
    }

    @Test
    void shouldThrowExceptionWhenARowHasTheWrongNumberOfColumns() {
        // Arrange
        String csv = HEADER + "INFANTRY,SOLDIER,Grunt,10,8\n";

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> UnitCatalogLoader.parse(streamOf(csv)));

        // Assert
        assertEquals("Invalid unit catalog data: line 2 has 5 columns, expected 8.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionOnNonNumericStat() {
        // Arrange
        String csv = HEADER + "INFANTRY,SOLDIER,Grunt,not-a-number,8,4,30,0\n";

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> UnitCatalogLoader.parse(streamOf(csv)));

        // Assert
        assertEquals("Invalid unit catalog data: line 2 contains a non-numeric stat.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionOnUnknownCategory() {
        // Arrange
        String csv = HEADER + "AIRCRAFT,COMBAT,Fighter,50,20,5,40,0\n";

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> UnitCatalogLoader.parse(streamOf(csv)));

        // Assert
        assertEquals("Invalid unit catalog data: line 2 has an unknown unit category \"AIRCRAFT\".", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCatalogIsEmpty() {
        // Arrange
        String csv = HEADER;

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class,
                () -> UnitCatalogLoader.parse(streamOf(csv)));

        // Assert
        assertEquals("Invalid unit catalog data: catalog file contains no unit rows.", e.getMessage());
    }

    @Test
    void shouldLoadTheBundledCatalogFile() throws IOException, InvalidUnitDataException {
        // Arrange
        UnitCatalogLoader loader = new UnitCatalogLoader("/units_catalog.csv");

        // Act
        List<UnitTemplate> templates = loader.findAll();

        // Assert
        assertEquals(12, templates.size());
    }

    @Test
    void shouldThrowExceptionWhenTheCatalogFileIsMissing() {
        // Arrange
        UnitCatalogLoader loader = new UnitCatalogLoader("/does_not_exist.csv");

        // Act
        InvalidUnitDataException e = assertThrows(InvalidUnitDataException.class, loader::findAll);

        // Assert
        assertEquals("Invalid unit catalog data: catalog file /does_not_exist.csv not found on classpath.",
                e.getMessage());
    }
}
