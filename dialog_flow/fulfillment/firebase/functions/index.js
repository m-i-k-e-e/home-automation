// See https://github.com/dialogflow/dialogflow-fulfillment-nodejs
// for Dialogflow fulfillment library docs, samples, and to report issues
'use strict';
 
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const {WebhookClient} = require('dialogflow-fulfillment');
const {Card, Suggestion} = require('dialogflow-fulfillment');
 
var config = {
	apiKey: "API KEY from Firebase parameters",	
	databaseURL: "firebase database",
	projectId: "firebase project id",	
	messagingSenderId: "Message sender id from Firebase -> parameters -> Cloud messaging"
};

process.env.DEBUG = 'dialogflow:debug'; // enables lib debugging statements
 
admin.initializeApp(config);

exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });
  console.log('Dialogflow Request headers: ' + JSON.stringify(request.headers));
  console.log('Dialogflow Request body: ' + JSON.stringify(request.body));
  
  function ventilation(agent){    
    var fan_speed = agent.parameters.ventilation_puissance;
    var fan_speed_code = 2;
    
    if (fan_speed >= 50 && fan_speed <= 99) fan_speed_code = 1;
    if (fan_speed >= 0 && fan_speed <= 49) fan_speed_code = 0;    
    
    admin.database().ref('vmc/fan/0').set({
    	speed:fan_speed_code
    });   
    agent.add("Fan speed has been updated")
    agent.add(new Suggestion("fans at 100%"));
  }

  let intentMap = new Map(); 
  intentMap.set('Ventilation', ventilation);  
  agent.handleRequest(intentMap);
});
