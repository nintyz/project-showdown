package com.projectshowdown.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an organizer in the application.
 * Stores details about the organizer's profile, verification status, and online presence.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {
    /**
     * The date the organizer's account was verified.
     * Stored as a string in ISO 8601 format (e.g., "2024-01-01T12:00:00").
     */
    String dateVerified;

    /**
     * A brief biography or description of the organizer.
     */
    String bio;

    /**
     * The country where the organizer is based.
     */
    String country;

    /**
     * A link to the organizer's website or online profile.
     */
    String websiteLink;

    /**
     * Checks whether the organizer's account is verified.
     *
     * @return {@code true} if the organizer is verified; {@code false} otherwise.
     */
    public boolean checkVerified() {
        return (getDateVerified() != null && !getDateVerified().equals("null"));

    }
}
