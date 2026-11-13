package com.armysimulator.exception;

public class InsufficientPointsException extends ArmySimulationException {

    private static final long serialVersionUID = 1L;

    // item describes what was added, e.g. unit "Grunt" or group "Veterans"
    public InsufficientPointsException(String item, int cost, int remainingPoints) {
        super("Cannot add " + item + " costing " + cost + " points: only " + remainingPoints + " points remaining.");
    }
}
