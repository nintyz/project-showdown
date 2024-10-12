// const admin = require('firebase-admin');
const functions = require('firebase-functions');
const mailjet = require('node-mailjet');

// Initialize Mailjet with API keys from environment variables
const mjClient = mailjet.apiConnect(functions.config().mailjet.apikey, functions.config().mailjet.secretkey);

// Dummy data for testing: user's email and match time
const dummyUserEmail = "pehsiewyubxy@gmail.com";
const dummyMatchTime = new Date('2024-10-12T12:36:00');  // Example match time (adjust as needed)
const dummyName = "Player";

// Firebase Pub/Sub function scheduled to run every 1 minute for testing purposes
exports.sendMatchEmailNotifications = functions.pubsub.schedule('every 1 minutes').onRun(async (context) => {
  
  const now = new Date();
  
  // Send notifications 30 minutes before the match
  const thirtyMinutesBefore = new Date(dummyMatchTime.getTime() - 30 * 60 * 1000);
  
  // Send notifications 10 minutes before the match
  const tenMinutesBefore = new Date(dummyMatchTime.getTime() - 10 * 60 * 1000);
  
  // If the current time is exactly 30 minutes before the match
  if (isSameMinute(now, thirtyMinutesBefore)) {
    sendEmailNotification(dummyUserEmail, dummyMatchTime, dummyName, "30 minutes");
  }
  
  // If the current time is exactly 10 minutes before the match
  if (isSameMinute(now, tenMinutesBefore)) {
    sendEmailNotification(dummyUserEmail, dummyMatchTime, dummyName, "10 minutes");
  }
  
  console.log('Notification check complete for', now);
});

// Helper function to check if two dates fall on the same minute
function isSameMinute(date1, date2) {
  return date1.getMinutes() === date2.getMinutes() &&
  date1.getHours() === date2.getHours() &&
  date1.getDate() === date2.getDate() &&
  date1.getMonth() === date2.getMonth() &&
  date1.getFullYear() === date2.getFullYear();
}

// Function to send an email notification using Mailjet
function sendEmailNotification(email, matchTime, name, when) {
  const request = mjClient.post('send', { version: 'v3.1' }).request({
    Messages: [{
      From: {
        Email: 'siewyu.peh.2023@scis.smu.edu.sg',
        Name: 'Showdown App'
      },
      To: [{
        Email: email,
        Name: name
      }],
      TemplateID: 6376567,
      TemplateLanguage: true,
      Variables: {   // Passing the 'when','name' and 'matchTime' variables to Mailjet
        "name": name,  // Replaces {{var:name}} in the template
        "when": when,       // Replaces {{var:when}} in the template
        "matchTime": matchTime.toLocaleString("en-SG", { timeZone: "Asia/Singapore" }) // Replaces {{var:matchTime}} in the template
      }
    }]
  });

  request.then((result) => {
    console.log('Email sent to:', email);
  }).catch((err) => {
    console.error('Error sending email to:', email, err);
  });
}


