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
    String name;
    boolean verified;
    String dateVerified;
    String bio;
    String country;
    String websiteLink;
}
