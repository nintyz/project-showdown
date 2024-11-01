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

        let userAge = 0
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

  let intentMap = new Map();
  // Player Intents
  intentMap.set('PlayerAge', getAgeByName);
  intentMap.set('PlayerElo', getEloByName);
  intentMap.set('PlayerRank', getRankByName);

  // Tournament Intents
  intentMap.set('TournamentDate', getDateByTournamentName);
  intentMap.set('TournamentVenue', getVenueByTournamentName);

  agent.handleRequest(intentMap);
});
