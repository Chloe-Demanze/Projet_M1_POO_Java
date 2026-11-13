package com.armysimulator.exception;

public class EmptyGroupException extends ArmySimulationException {

    private static final long serialVersionUID = 1L;

    public EmptyGroupException(String groupName) {
        super("Group \"" + groupName + "\" has no living units and cannot take part in a battle.");
    }
}
