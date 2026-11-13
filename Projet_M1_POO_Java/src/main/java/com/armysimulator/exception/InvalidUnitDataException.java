package com.armysimulator.exception;

public class InvalidUnitDataException extends ArmySimulationException {

    private static final long serialVersionUID = 1L;

    public InvalidUnitDataException(String reason) {
        super("Invalid unit catalog data: " + reason);
    }

    public InvalidUnitDataException(String reason, Throwable cause) {
        super("Invalid unit catalog data: " + reason, cause);
    }
}
