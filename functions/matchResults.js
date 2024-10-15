const functions = require('firebase-functions');
const mailjet = require('node-mailjet');

// Initialize Mailjet with API keys from environment variables
const mjClient = mailjet.apiConnect(functions.config().mailjet.apikey, functions.config().mailjet.secretkey);

// Dummy data for saved matches, match end times, and updated status
const savedMatches = {
  "pehsiewyubxy@gmail.com": ["Match 1", "Match 2"],  // User 1 saved two matches
  "pehssiewyu@gmail.com": ["Match 3"]              // User 2 saved one match
};

const matchStatus = {
  "Match 1": { ended: false, sent: false },  // Match 1 results not yet updated
  "Match 2": { ended: true, sent: false },   // Match 2 results updated
  "Match 3": { ended: false, sent: false }   // Match 3 results not yet updated
};

// Firebase Pub/Sub function to check saved matches and send notifications
exports.sendMatchResultNotification = functions.pubsub.schedule('every 1 minutes').onRun(async (context) => {
    // Iterate through each user and check their saved matches
    for (const [email, matches] of Object.entries(savedMatches)) {
    checkSavedMatches(email, matches);
    }
});

// Method to check saved matches for a user
function checkSavedMatches(email, matches) {
    matches.forEach(matchName => {
    // Check if the match results have been updated
    if (checkIfEnded(matchName)) {
        // If the match has been updated, send the email notification
        sendMatchResultNotification(email, "Player", matchName);
        // Mark the email as sent so it doesn't send again
        matchStatus[matchName].sent = true;
    }
    });
    }

// Helper function to check if a match result has been updated
function checkIfEnded(matchName) {
    const match = matchStatus[matchName];

    // Return true if the "updated" variable is true for the match
    return match && match.ended === true && match.sent == false;
}

// Helper function to send email notifications for match results
function sendMatchResultNotification(email, name, matchName) {
    const templateID = 6376696;  // Replace with your Mailjet template ID

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
        TemplateID: 6376696,
        TemplateLanguage: true,
        Variables: {
        "name": name,
        }
    }]
    });

    request.then((result) => {
    console.log(`Match result email sent to: ${email} for ${matchName}`);
    }).catch((err) => {
    console.error(`Error sending email to: ${email} for ${matchName}`, err);
    });
}

