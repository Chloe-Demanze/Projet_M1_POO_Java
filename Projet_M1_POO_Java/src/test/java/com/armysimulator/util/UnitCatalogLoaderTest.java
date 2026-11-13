package com.armysimulator.util;

import com.armysimulator.exception.InvalidUnitDataException;
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

    private final UnitCatalogLoader loader = new UnitCatalogLoader();

    private static InputStream streamOf(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldParseValidCatalogRows() throws IOException, InvalidUnitDataException {
        String csv = "category,subtype,name,cost,attack,defense,health,capacity\n"
                + "INFANTRY,SOLDIER,Grunt,10,8,4,30,0\n"
                + "VEHICLE,TRANSPORT,Troop Carrier,35,2,10,80,6\n";

        List<UnitTemplate> templates = loader.load(streamOf(csv));

        assertEquals(2, templates.size());
        UnitTemplate grunt = templates.get(0);
        assertEquals("Grunt", grunt.getName());
        assertEquals(10, grunt.getCost());
        assertEquals(30, grunt.getHealth());
        UnitTemplate carrier = templates.get(1);
        assertEquals(6, carrier.getCapacity());
    }

    @Test
    void shouldNormalizeCategoryAndSubtypeToUpperCase() throws IOException, InvalidUnitDataException {
        String csv = "category,subtype,name,cost,attack,defense,health,capacity\n"
                + " infantry , soldier ,Grunt,10,8,4,30,0\n";

        UnitTemplate grunt = loader.load(streamOf(csv)).get(0);

        assertEquals("INFANTRY", grunt.getCategory());
        assertEquals("SOLDIER", grunt.getSubtype());
    }

    @Test
    void shouldThrowExceptionWhenARowHasTheWrongNumberOfColumns() {
        String csv = "category,subtype,name,cost,attack,defense,health,capacity\n"
                + "INFANTRY,SOLDIER,Grunt,10,8\n";

        assertThrows(InvalidUnitDataException.class, () -> loader.load(streamOf(csv)));
    }

    @Test
    void shouldThrowExceptionOnMalformedRow() {
        String csv = "category,subtype,name,cost,attack,defense,health,capacity\n"
                + "INFANTRY,SOLDIER,Grunt,not-a-number,8,4,30,0\n";

        assertThrows(InvalidUnitDataException.class, () -> loader.load(streamOf(csv)));
    }

    @Test
    void shouldThrowExceptionWhenCatalogIsEmpty() {
        String csv = "category,subtype,name,cost,attack,defense,health,capacity\n";

        assertThrows(InvalidUnitDataException.class, () -> loader.load(streamOf(csv)));
    }
}
