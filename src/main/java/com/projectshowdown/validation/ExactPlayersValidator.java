// package com.projectshowdown.validation;

// import jakarta.validation.ConstraintValidator;
// import jakarta.validation.ConstraintValidatorContext;

// import com.projectshowdown.entities.Tournament;
// import com.projectshowdown.entities.Player;
// import com.projectshowdown.entities.User;

// import java.util.ArrayList;

// public class ExactPlayersValidator implements ConstraintValidator<ExactPlayers, Tournament> {

//     @Override
//     public void initialize(ExactPlayers constraintAnnotation) {
//         // No initialization required
//     }

//     @Override
//     public boolean isValid(Tournament tournament, ConstraintValidatorContext context) {
//         if (tournament == null || tournament.getUsers() == null) {
//             return true; // If the tournament or users list is null, we let other validations handle it
//         }

//         // Get the expected number of players from the tournament
//         int expectedNumPlayers = tournament.getNumPlayers();
//         ArrayList<User> players = tournament.getUsers(); // Cast the users list to a list of players

//         // Validate that the number of players matches the expected number
//         return players.size() == expectedNumPlayers;
//     }
// }