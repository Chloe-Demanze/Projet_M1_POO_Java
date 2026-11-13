package com.armysimulator.service;

import com.armysimulator.model.UnitTemplate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Units available in the catalog, sorted by category
public class UnitCatalogService {

    private final Map<String, List<UnitTemplate>> templatesByCategory;

    public UnitCatalogService(List<UnitTemplate> templates) {
        this.templatesByCategory = templates.stream()
                .collect(Collectors.groupingBy(UnitTemplate::getCategory, TreeMap::new, Collectors.toUnmodifiableList()));
    }

    public List<String> getCategories() {
        return List.copyOf(templatesByCategory.keySet());
    }

    public List<UnitTemplate> getTemplates(String category) {
        return templatesByCategory.getOrDefault(category, List.of());
    }
}
