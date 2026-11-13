package com.armysimulator.service;

import com.armysimulator.exception.InvalidUnitDataException;
import com.armysimulator.model.UnitTemplate;

import java.io.IOException;
import java.util.List;

// Where the unit templates come from. The services depend on this interface,
// not on the csv reader (DIP). Tests can pass a lambda returning a fixed list.
@FunctionalInterface
public interface UnitCatalogRepository {

    List<UnitTemplate> findAll() throws IOException, InvalidUnitDataException;
}
