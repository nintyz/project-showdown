'use strict';

const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

process.env.DEBUG = 'dialogflow:debug'; 

exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });

  function getEmailByName(agent) {
    const userName = agent.parameters.name;

    if (!userName) {
      agent.add("Please provide a name.");
      return;
    }

    return db.collection('users')
      .where('playerDetails.name', '==', userName.name)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          agent.add(`No user found with the name: ${userName.name}`);
          return;
        }

        let userEmail = '';
        snapshot.forEach(doc => {
          userEmail = doc.data().email;
        });

        agent.add(`The email for ${userName.name} is: ${userEmail}`);
      })
      .catch(error => {
        console.error('Error retrieving Firestore documents:', error);
        agent.add('Error retrieving data from Firestore: ' + error.message);
      });
  }

  let intentMap = new Map();
  intentMap.set('GetEmail', getEmailByName);

  agent.handleRequest(intentMap);
});
