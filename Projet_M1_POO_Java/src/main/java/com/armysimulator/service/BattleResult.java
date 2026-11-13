package com.armysimulator.service;

import java.util.List;
import java.util.Optional;

public class BattleResult {

    private final String winner; // null when the battle is a draw
    private final int rounds;
    private final List<String> log;

    public BattleResult(String winner, int rounds, List<String> log) {
        this.winner = winner;
        this.rounds = rounds;
        this.log = List.copyOf(log);
    }

    public Optional<String> getWinner() {
        return Optional.ofNullable(winner);
    }

    public int getRounds() {
        return rounds;
    }

    public List<String> getLog() {
        return log;
    }
}
