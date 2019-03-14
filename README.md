# [WIP] Home Automation
## Workflow
### Graph
![Graph](https://github.com/m-i-k-e-e/home-automation/blob/master/graph.png?raw=true "graph")
### Assistant flow
1. A Google Assistant is handled by DialogFlow.
1. The DialogFlow script updates the Firebase Realtime Database with the requested speed.
1. The Raspberry Pi handles the database change and sends a request to the Arduino.
1. The arduino set the speed of the MVHR.
### Phone flow
1. The phone app updates the Firebase Realtime Database with the requested speed.
1. The Raspberry Pi handles the database change and sends a request to the Arduino.
1. The arduino set the speed of the MVHR.
### Alternate phone flow
If Firebase returns an error, the phone app will try to send the update directly to the arduino.
## Dialog Flow
[/dialog_flow](https://github.com/m-i-k-e-e/home-automation/blob/master/dialog_flow)
## Arduino
[/arduino](https://github.com/m-i-k-e-e/home-automation/tree/master/arduino)
## HomeConnect
[/HomeConnect](https://github.com/m-i-k-e-e/home-automation/blob/master/HomeConnect)
