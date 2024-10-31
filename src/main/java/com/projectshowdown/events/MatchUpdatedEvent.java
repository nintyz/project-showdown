package com.projectshowdown.events;

import org.springframework.context.ApplicationEvent;

public class MatchUpdatedEvent extends ApplicationEvent {
    private final String tournamentId;
    // Add more fields if necessary

    public MatchUpdatedEvent(Object source, String tournamentId) {
        super(source);
        this.tournamentId = tournamentId;
    }

    public String getTournamentId() {
        return tournamentId;
    }
}
