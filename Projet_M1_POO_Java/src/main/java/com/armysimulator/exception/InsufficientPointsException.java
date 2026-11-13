package com.armysimulator.exception;

public class InsufficientPointsException extends ArmySimulationException {

    private static final long serialVersionUID = 1L;

    public InsufficientPointsException(int cost, int remainingPoints) {
        super("Cannot add a unit costing " + cost + " points: only " + remainingPoints + " points remaining.");
    }
}
