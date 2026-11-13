package com.armysimulator.service;

import java.util.List;
import java.util.Optional;

public class BattleResult {

    private final Optional<String> winner;
    private final int rounds;
    private final List<String> log;

    public BattleResult(Optional<String> winner, int rounds, List<String> log) {
        this.winner = winner;
        this.rounds = rounds;
        this.log = log;
    }

    public Optional<String> getWinner() {
        return winner;
    }

    public int getRounds() {
        return rounds;
    }

    public List<String> getLog() {
        return log;
    }
}
