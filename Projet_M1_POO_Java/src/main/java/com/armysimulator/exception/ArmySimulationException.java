package com.armysimulator.exception;

// Parent of all our exceptions. Checked so we have to handle them.
public abstract class ArmySimulationException extends Exception {

    private static final long serialVersionUID = 1L;

    protected ArmySimulationException(String message) {
        super(message);
    }

    protected ArmySimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}
