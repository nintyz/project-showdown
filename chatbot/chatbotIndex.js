'use strict';

const functions = require('firebase-functions');
const { WebhookClient } = require('dialogflow-fulfillment');
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

process.env.DEBUG = 'dialogflow:debug';

exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });

  // Get Player's Age by Name
  function getAgeByName(agent) {
    const userName = agent.parameters.name_age;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    return db.collection('users')
      .where('playerDetails.name', '==', userName.name)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        let userAge = 0;
        snapshot.forEach(doc => {
          let dob = doc.data().playerDetails.dob;

          userAge = calculateAge(dob);
        });

        agent.add(`${userName.name} is ${userAge} years old.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Helper function to calculate age based on DOB
  function calculateAge(dob) {
    const dobDate = new Date(dob);
    const today = new Date();

    let age = today.getFullYear() - dobDate.getFullYear();
    const monthDiff = today.getMonth() - dobDate.getMonth();

    // Decrement age by 1 if the user's birthday has not yet occurred this year.
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dobDate.getDate())) {
      age--;
    }

    return age;
  }

  // Get Player's Elo by Name
  function getEloByName(agent) {
    const userName = agent.parameters.name_elo;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    return db.collection('users')
      .where('playerDetails.name', '==', userName.name)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        let userElo = 0;
        snapshot.forEach(doc => {
          userElo = doc.data().playerDetails.elo;
        });

        agent.add(`${userName.name} has an elo rating of ${userElo}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Get Player's Rank by Name
  function getRankByName(agent) {
    const userName = agent.parameters.name_rank;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    return db.collection('users')
      .where('playerDetails.name', '==', userName.name)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        let userRank = 0;
        snapshot.forEach(doc => {
          userRank = doc.data().playerDetails.rank;
        });

        agent.add(`${userName.name} is rank ${userRank}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Get Date by Tournament Name
  function getDateByTournamentName(agent) {
    const tournamentName = agent.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    return db.collection('tournaments')
      .where('name', '==', tournamentName)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        // Since there's only one date per tournament, we can directly retrieve it
        const doc = snapshot.docs[0]; // Get the first document
        const tournamentDate = doc.data().date; // Get the date from the document
        const formattedDate = formatDate(tournamentDate); // Format the date

        agent.add(`${tournamentName} is held on ${formattedDate}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Helper function to format date
  function formatDate(dateString) {
    const date = new Date(dateString);

    const options = { day: 'numeric', month: 'long', year: 'numeric' };

    return date.toLocaleDateString('en-GB', options);
  }

  // Get Date by Tournament Name
  function getVenueByTournamentName(agent) {
    const tournamentName = agent.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    return db.collection('tournaments')
      .where('name', '==', tournamentName)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        let tournamentVenue = "";
        snapshot.forEach(doc => {
          tournamentVenue = doc.data().venue;
        });

        agent.add(`${tournamentName} is held at ${tournamentVenue}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Winner of a Tournament
  function getTournamentWinner(agent) {
    const tournamentName = agent.parameters.tournament_name;
  
    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }
  
    return db.collection('tournaments')
      .where('name', '==', tournamentName)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }
  
        let tournamentId = "";
        snapshot.forEach(doc => {
          tournamentId = doc.data().id;
        });
  
        return db.collection('matches')
          .where('tournamentId', '==', tournamentId)
          .where('stage', '==', 'Finals')
          .get()
          .then(matchSnapshot => {
            if (matchSnapshot.empty) {
              agent.add(`No matches found for the tournament finals: "${tournamentName}".`);
              return;
            }
  
            const matchDoc = matchSnapshot.docs[0].data();
            const player1Id = matchDoc.player1Id;
            const player2Id = matchDoc.player2Id;
            const player1Score = matchDoc.player1Score;
            const player2Score = matchDoc.player2Score;
  
            // Determine winner
            const isPlayer1Winner = player1Score > player2Score;
            const winnerId = isPlayer1Winner ? player1Id : player2Id;
  
            const userPromises = [player1Id, player2Id].map(playerId => {
              return db.collection('users')
                .doc(playerId)
                .get()
                .then(userDoc => {
                  if (userDoc.exists) {
                    return userDoc.data().playerDetails.name;
                  } else {
                    return null;
                  }
                });
            });
  
            return Promise.all(userPromises)
              .then(names => {
                const validNames = names.filter(name => name !== null);
  
                if (validNames.length < 2) {
                  agent.add(`Could not retrieve all player names for the tournament finals: "${tournamentName}".`);
                  return;
                }
  
                const winnerName = validNames[isPlayer1Winner ? 0 : 1]; // Get the name of the winner
  
                agent.add(`The winner of the ${tournamentName} is ${winnerName}!`);
              });
          });
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Players in the Finals
  function getTournamentFinalsPlayers(agent) {
    const tournamentName = agent.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    return db.collection('tournaments')
      .where('name', '==', tournamentName)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        let tournamentId = "";
        snapshot.forEach(doc => {
          tournamentId = doc.data().id;
        });

        return db.collection('matches')
          .where('tournamentId', '==', tournamentId)
          .where('stage', '==', 'Finals')
          .get()
          .then(matchSnapshot => {
            if (matchSnapshot.empty) {
              agent.add(`No players found for the tournament finals: "${tournamentName}".`);
              return;
            }

            let playerIds = [];
            matchSnapshot.forEach(matchDoc => {
              playerIds.push(matchDoc.data().player1Id);
              playerIds.push(matchDoc.data().player2Id);
            });

            const userPromises = playerIds.map(playerId => {
              return db.collection('users')
                .doc(playerId)
                .get()
                .then(userDoc => {
                  if (userDoc.exists) {
                    return userDoc.data().playerDetails.name;
                  } else {
                    return null;
                  }
                });
            });

            return Promise.all(userPromises)
              .then(names => {
                const validNames = names.filter(name => name !== null);

                if (validNames.length === 0) {
                  agent.add(`No players found for the tournament finals: "${tournamentName}".`);
                } else {
                  agent.add(`The players in the "${tournamentName}" tournament finals are ${validNames.join(' and ')}.`);
                }
              });
          });
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

// Get Score of the Finals
function getTournamentFinalsScore(agent) {
  const tournamentName = agent.parameters.tournament_name;

  if (!tournamentName) {
    agent.add("Please provide a tournament name.");
    return;
  }

  return db.collection('tournaments')
    .where('name', '==', tournamentName)
    .get()
    .then(snapshot => {
      if (snapshot.empty) {
        agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
        return;
      }

      let tournamentId = "";
      snapshot.forEach(doc => {
        tournamentId = doc.data().id;
      });

      return db.collection('matches')
        .where('tournamentId', '==', tournamentId)
        .where('stage', '==', 'Finals')
        .get()
        .then(matchSnapshot => {
          if (matchSnapshot.empty) {
            agent.add(`No matches found for the tournament finals: "${tournamentName}".`);
            return;
          }

          const matchDoc = matchSnapshot.docs[0].data();
          const player1Id = matchDoc.player1Id;
          const player2Id = matchDoc.player2Id;
          const player1Score = matchDoc.player1Score;
          const player2Score = matchDoc.player2Score;

          // Determine winner
          const isPlayer1Winner = player1Score > player2Score;
          const winnerId = isPlayer1Winner ? player1Id : player2Id;

          const userPromises = [player1Id, player2Id].map(playerId => {
            return db.collection('users')
              .doc(playerId)
              .get()
              .then(userDoc => {
                if (userDoc.exists) {
                  return userDoc.data().playerDetails.name;
                } else {
                  return null;
                }
              });
          });

          return Promise.all(userPromises)
            .then(names => {
              const validNames = names.filter(name => name !== null);

              if (validNames.length < 2) {
                agent.add(`Could not retrieve all player names for the tournament finals: "${tournamentName}".`);
                return;
              }

              const winnerName = validNames[isPlayer1Winner ? 0 : 1]; // Get the name of the winner

              agent.add(`In the "${tournamentName}" finals, ${validNames[0]} scored ${player1Score} and ${validNames[1]} scored ${player2Score}. The winner is ${winnerName}!`);
            });
        });
    })
    .catch(error => {
      console.error('Error retrieving Firestore documents:', error);
      agent.add('There was an error retrieving data from Firestore. Please try again later.');
    });
  }

  let intentMap = new Map();
  // Player Intents
  intentMap.set('PlayerAge', getAgeByName);
  intentMap.set('PlayerElo', getEloByName);
  intentMap.set('PlayerRank', getRankByName);

  // Tournament Intents
  intentMap.set('TournamentDate', getDateByTournamentName);
  intentMap.set('TournamentVenue', getVenueByTournamentName);
  intentMap.set('TournamentWinner', getTournamentWinner);

  // Match Intents
  intentMap.set('MatchFinalsPlayers', getTournamentFinalsPlayers);
  intentMap.set('MatchFinalsScore', getTournamentFinalsScore);

  agent.handleRequest(intentMap);
});
