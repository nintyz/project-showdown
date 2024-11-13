package com.projectshowdown.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organizer {
    String dateVerified;
    String bio;
    String country;
    String websiteLink;

    public boolean checkVerified() {
        return (getDateVerified() != null && !getDateVerified().equals("null"));

    }
}
