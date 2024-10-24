'use strict';

const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
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
          userAge = doc.data().playerDetails.age;
        });

        agent.add(`${userName.name} is ${userAge} years old.`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
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

  let intentMap = new Map();
  intentMap.set('PlayerAge', getAgeByName);
  intentMap.set('PlayerElo', getEloByName);
  intentMap.set('PlayerRank', getRankByName);

  agent.handleRequest(intentMap);
});
