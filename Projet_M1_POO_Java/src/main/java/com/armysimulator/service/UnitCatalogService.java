package com.armysimulator.service;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.UnitCategory;
import com.armysimulator.model.UnitTemplate;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Units available in the catalog, sorted by category.
// Loads the rows from a repository and checks every one of them at startup.
public class UnitCatalogService {

    private final Map<UnitCategory, List<UnitTemplate>> templatesByCategory;

    public UnitCatalogService(UnitCatalogRepository repository, UnitFactory unitFactory)
            throws IOException, InvalidUnitDataException {
        List<UnitTemplate> templates = repository.findAll();
        unitFactory.checkCatalog(templates);
        this.templatesByCategory = templates.stream()
                .collect(Collectors.groupingBy(UnitTemplate::getCategory,
                        () -> new EnumMap<>(UnitCategory.class), Collectors.toUnmodifiableList()));
    }

    public List<UnitCategory> getCategories() {
        return List.copyOf(templatesByCategory.keySet());
    }

    public List<UnitTemplate> getTemplates(UnitCategory category) {
        return templatesByCategory.getOrDefault(category, List.of());
    }
}
