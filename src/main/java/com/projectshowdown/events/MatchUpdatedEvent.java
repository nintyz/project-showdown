package com.projectshowdown.events;

import org.springframework.context.ApplicationEvent;

import com.projectshowdown.entities.Match;

public class MatchUpdatedEvent extends ApplicationEvent {
    private final String tournamentId;
    private final Match match;
    // Add more fields if necessary

    public MatchUpdatedEvent(Object source, String tournamentId, Match match) {
        super(source);
        this.tournamentId = tournamentId;
        this.match = match;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public Match getMatch() {
        return match;
    }
}
