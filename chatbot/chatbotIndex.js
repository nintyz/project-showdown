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

    agent.setContext({
      name: 'player_age_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const dob = foundUser.data().playerDetails.dob;
        const userAge = calculateAge(dob);
        agent.add(`${foundUser.data().name} is ${userAge} years old.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Get Player's Age by Name (Context)
  function getAgeByName_Context(agent) {
    const eloContext = agent.getContext('player_elo_context');
    const rankContext = agent.getContext('player_rank_context');

    // Determine which context to use based on lifespan
    let context;
    if (eloContext && rankContext) {
      context = eloContext.lifespan > rankContext.lifespan ? eloContext : rankContext;
    } else {
      context = eloContext || rankContext;
    }

    const userName = context.parameters.player_name;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    agent.setContext({
      name: 'player_age_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const dob = foundUser.data().playerDetails.dob;
        const userAge = calculateAge(dob);
        agent.add(`${foundUser.data().name} is ${userAge} years old.`);
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

    agent.setContext({
      name: 'player_elo_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const userElo = foundUser.data().playerDetails.elo;
        agent.add(`${foundUser.data().name} has an elo rating of ${userElo}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Get Player's Elo by Name (Context)
  function getEloByName_Context(agent) {
    const ageContext = agent.getContext('player_age_context');
    const rankContext = agent.getContext('player_rank_context');

    // Determine which context to use based on lifespan
    let context;
    if (ageContext && rankContext) {
      context = ageContext.lifespan > rankContext.lifespan ? ageContext : rankContext;
    } else {
      context = ageContext || rankContext;
    }

    const userName = context.parameters.player_name;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    agent.setContext({
      name: 'player_elo_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const userElo = foundUser.data().playerDetails.elo;
        agent.add(`${foundUser.data().name} has an elo rating of ${userElo}.`);
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

    agent.setContext({
      name: 'player_rank_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const userRank = foundUser.data().playerDetails.rank;
        agent.add(`${foundUser.data().name} is rank ${userRank}.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  // Get Player's Rank by Name (Context)
  function getRankByName_Context(agent) {
    const ageContext = agent.getContext('player_age_context');
    const eloContext = agent.getContext('player_elo_context');

    // Determine which context to use based on lifespan
    let context;
    if (ageContext && eloContext) {
      context = ageContext.lifespan > eloContext.lifespan ? ageContext : eloContext;
    } else {
      context = ageContext || eloContext;
    }

    const userName = context.parameters.player_name;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    agent.setContext({
      name: 'player_rank_context',
      lifespan: 5,
      parameters: { player_name: userName }
    });

    return db.collection('users')
      .get()
      .then(snapshot => {
        let foundUser = null;

        snapshot.forEach(doc => {
          const storedName = doc.data().name;
          if (storedName.toLowerCase() === userName.name.toLowerCase()) {
            foundUser = doc;
          }
        });

        if (!foundUser) {
          agent.add(`We couldn't find any user with the name: ${userName.name}. Please check the name and try again.`);
          return;
        }

        const userRank = foundUser.data().playerDetails.rank;
        agent.add(`${foundUser.data().name} is rank ${userRank}.`);
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

    agent.setContext({
      name: 'tournament_date_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentDate = foundTournament.data().dateTime;
        const formattedDate = formatDate(tournamentDate);
        const resultTournamentName = foundTournament.data().name;

        // Check if the tournament date is in the future or past
        const tournamentDateObj = new Date(tournamentDate);
        const currentDate = new Date();

        if (tournamentDateObj > currentDate) {
          agent.add(`${resultTournamentName} is held on ${formattedDate}.`);
        } else {
          agent.add(`${resultTournamentName} was held on ${formattedDate}.`);
        }
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Date by Tournament Name (Context)
  function getDateByTournamentName_Context(agent) {
    const venueContext = agent.getContext('tournament_venue_context');
    const winnerContext = agent.getContext('tournament_winner_context');

    // Determine which context to use based on lifespan
    let context;
    if (winnerContext && venueContext) {
      context = winnerContext.lifespan > venueContext.lifespan ? winnerContext : venueContext;
    } else {
      context = winnerContext || venueContext;
    }

    const tournamentName = context.parameters.tournament_name;
    if (!tournamentName) {
      agent.add(`Which tournament are you interested in?`);
      return;
    }

    agent.setContext({
      name: 'tournament_date_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentDate = foundTournament.data().dateTime;
        const formattedDate = formatDate(tournamentDate);
        const resultTournamentName = foundTournament.data().name;

        // Check if the tournament date is in the future or past
        const tournamentDateObj = new Date(tournamentDate);
        const currentDate = new Date();

        if (tournamentDateObj > currentDate) {
          agent.add(`${resultTournamentName} is held on ${formattedDate}.`);
        } else {
          agent.add(`${resultTournamentName} was held on ${formattedDate}.`);
        }
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Helper function to format date
  function formatDate(dateString) {
    const date = new Date(dateString);

    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'long' });
    const year = date.getFullYear();

    return `${day} ${month} ${year}`;
  }

  // Get Venue by Tournament Name
  function getVenueByTournamentName(agent) {
    const tournamentName = agent.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    agent.setContext({
      name: 'tournament_venue_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentVenue = foundTournament.data().venue;
        const resultTournamentName = foundTournament.data().name;
        const tournamentDate = foundTournament.data().dateTime;
        const tournamentDateObj = new Date(tournamentDate);
        const currentDate = new Date();

        // Check if the tournament date is in the future or past
        if (tournamentDateObj > currentDate) {
          agent.add(`${resultTournamentName} will be held at ${tournamentVenue}.`);
        } else {
          agent.add(`${resultTournamentName} was held at ${tournamentVenue}.`);
        }
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Venue by Tournament Name (Context)
  function getVenueByTournamentName_Context(agent) {
    const dateContext = agent.getContext('tournament_date_context');
    const winnerContext = agent.getContext('tournament_winner_context');

    // Determine which context to use based on lifespan
    let context;
    if (winnerContext && dateContext) {
      context = winnerContext.lifespan > dateContext.lifespan ? winnerContext : dateContext;
    } else {
      context = winnerContext || dateContext;
    }

    const tournamentName = context.parameters.tournament_name;

    if (!tournamentName) {
      agent.add(`Which tournament are you interested in?`);
      return;
    }

    agent.setContext({
      name: 'tournament_venue_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentVenue = foundTournament.data().venue;
        const resultTournamentName = foundTournament.data().name;
        const tournamentDate = foundTournament.data().dateTime;
        const tournamentDateObj = new Date(tournamentDate);
        const currentDate = new Date();

        // Check if the tournament date is in the future or past
        if (tournamentDateObj > currentDate) {
          agent.add(`${resultTournamentName} will be held at ${tournamentVenue}.`);
        } else {
          agent.add(`${resultTournamentName} was held at ${tournamentVenue}.`);
        }
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

    agent.setContext({
      name: 'tournament_winner_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentId = foundTournament.data().id;
        const resultTournamentName = foundTournament.data().name;

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
                    return userDoc.data().name;
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

                agent.add(`The winner of the ${resultTournamentName} was ${winnerName}!`);
              });
          });
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Winner of a Tournament (Context)
  function getTournamentWinner_Context(agent) {
    const venueContext = agent.getContext('tournament_venue_context');
    const dateContext = agent.getContext('tournament_date_context');

    // Determine which context to use based on lifespan
    let context;
    if (venueContext && dateContext) {
      context = venueContext.lifespan > dateContext.lifespan ? venueContext : dateContext;
    } else {
      context = venueContext || dateContext;
    }

    const tournamentName = context.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    agent.setContext({
      name: 'tournament_winner_context',
      lifespan: 5,
      parameters: { tournament_name: tournamentName }
    });

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentId = foundTournament.data().id;
        const resultTournamentName = foundTournament.data().name;

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
                    return userDoc.data().name;
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

                agent.add(`The winner of the ${resultTournamentName} was ${winnerName}!`);
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
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentId = foundTournament.data().id;
        const resultTournamentName = foundTournament.data().name;

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
                    return userDoc.data().name;
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
                  agent.add(`The players in the ${resultTournamentName} tournament finals are ${validNames.join(' and ')}.`);
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
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentId = foundTournament.data().id;
        const resultTournamentName = foundTournament.data().name;

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
                    return userDoc.data().name;
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

                agent.add(`In the ${resultTournamentName} finals, ${validNames[0]} scored ${player1Score} and ${validNames[1]} scored ${player2Score}. The winner was ${winnerName}!`);
              });
          });
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('There was an error retrieving data from Firestore. Please try again later.');
      });
  }

  // Get Score of the Finals (Context)
  function getTournamentFinalsScore_Context(agent) {
    const context = agent.getContext('tournament_winner_context');

    const tournamentName = context.parameters.tournament_name;

    if (!tournamentName) {
      agent.add("Please provide a tournament name.");
      return;
    }

    return db.collection('tournaments')
      .get()
      .then(snapshot => {
        let foundTournament = null;

        snapshot.forEach(doc => {
          const storedTournamentName = doc.data().name;
          if (storedTournamentName.toLowerCase() === tournamentName.toLowerCase()) {
            foundTournament = doc;
          }
        });

        if (!foundTournament) {
          agent.add(`We couldn't find any tournament with the name: "${tournamentName}". Please check the name and try again.`);
          return;
        }

        const tournamentId = foundTournament.data().id;
        const resultTournamentName = foundTournament.data().name;

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
                    return userDoc.data().name;
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

                agent.add(`In the ${resultTournamentName} finals, ${validNames[0]} scored ${player1Score} and ${validNames[1]} scored ${player2Score}. The winner was ${winnerName}!`);
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
  intentMap.set('PlayerAge_Context', getAgeByName_Context);
  intentMap.set('PlayerElo', getEloByName);
  intentMap.set('PlayerElo_Context', getEloByName_Context);
  intentMap.set('PlayerRank', getRankByName);
  intentMap.set('PlayerRank_Context', getRankByName_Context);

  // Tournament Intents
  intentMap.set('TournamentDate', getDateByTournamentName);
  intentMap.set('TournamentDate_Context', getDateByTournamentName_Context);
  intentMap.set('TournamentVenue', getVenueByTournamentName);
  intentMap.set('TournamentVenue_Context', getVenueByTournamentName_Context);
  intentMap.set('TournamentWinner', getTournamentWinner);
  intentMap.set('TournamentWinner_Context', getTournamentWinner_Context);

  // Match Intents
  intentMap.set('MatchFinalsPlayers', getTournamentFinalsPlayers);
  intentMap.set('MatchFinalsScore', getTournamentFinalsScore);
  intentMap.set('MatchFinalsScore_Context', getTournamentFinalsScore_Context);

  agent.handleRequest(intentMap);
});