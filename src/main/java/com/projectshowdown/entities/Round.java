package com.projectshowdown.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a round in a tournament.
 * A round contains a name and a list of match IDs associated with it.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Round {
    /**
     * The name of the round (e.g., "Quarterfinals", "Semifinals").
     */
    String name;

    /**
     * A list of match IDs associated with this round.
     */
    List<String> matches;
}
