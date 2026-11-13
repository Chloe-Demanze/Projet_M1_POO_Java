package com.armysimulator.util;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;
import com.armysimulator.service.UnitCatalogRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Reads units_catalog.csv from the classpath. Only checks the format here, the values are checked by the model.
public class UnitCatalogLoader implements UnitCatalogRepository {

    private static final int EXPECTED_COLUMNS = 8;

    private final String resourcePath;

    public UnitCatalogLoader(String resourcePath) {
        this.resourcePath = Objects.requireNonNull(resourcePath, "Resource path must not be null.");
    }

    @Override
    public List<UnitTemplate> findAll() throws IOException, InvalidUnitDataException {
        try (InputStream csvStream = UnitCatalogLoader.class.getResourceAsStream(resourcePath)) {
            if (csvStream == null) {
                throw new InvalidUnitDataException("catalog file " + resourcePath + " not found on classpath.");
            }
            return parse(csvStream);
        }
    }

    // package-private so the tests can parse a csv given as a string
    static List<UnitTemplate> parse(InputStream csvStream) throws IOException, InvalidUnitDataException {
        List<UnitTemplate> templates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false;
                    continue; // header row
                }
                if (line.isBlank()) {
                    continue;
                }
                templates.add(parseLine(line, lineNumber));
            }
        }
        if (templates.isEmpty()) {
            throw new InvalidUnitDataException("catalog file contains no unit rows.");
        }
        return templates;
    }

    private static UnitTemplate parseLine(String line, int lineNumber) throws InvalidUnitDataException {
        String[] columns = line.split(",", -1);
        if (columns.length != EXPECTED_COLUMNS) {
            throw new InvalidUnitDataException("line " + lineNumber + " has " + columns.length
                    + " columns, expected " + EXPECTED_COLUMNS + ".");
        }
        UnitCategory category = parseCategory(columns[0], lineNumber);
        try {
            return new UnitTemplate(
                    category,
                    columns[1].trim().toUpperCase(Locale.ROOT),
                    columns[2].trim(),
                    Integer.parseInt(columns[3].trim()),
                    Integer.parseInt(columns[4].trim()),
                    Integer.parseInt(columns[5].trim()),
                    Integer.parseInt(columns[6].trim()),
                    Integer.parseInt(columns[7].trim())
            );
        } catch (NumberFormatException e) {
            throw new InvalidUnitDataException("line " + lineNumber + " contains a non-numeric stat.", e);
        }
    }

    private static UnitCategory parseCategory(String column, int lineNumber) throws InvalidUnitDataException {
        String value = column.trim().toUpperCase(Locale.ROOT);
        try {
            return UnitCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidUnitDataException("line " + lineNumber + " has an unknown unit category \"" + value + "\".", e);
        }
    }
}
