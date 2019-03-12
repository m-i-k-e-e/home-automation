#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h> 

//const char* wifiName = "ArthurDent.2.4";
const char* wifiName = "parityboot-b";
const char* wifiPass = "Mick871977";
const char* server_local_name = "ventilation";

const int U1 = 15;
const int U3 = 13;
const int TMP = A0;

ESP8266WebServer server(80);
IPAddress ip(192, 168, 1, 10);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);

void handleNotFound() {
  digitalWrite(LED_BUILTIN, LOW);
  String message = "{\"error_code\":404}";
  server.send(404, "text/plain", message);
  digitalWrite(LED_BUILTIN, HIGH);
}

void setup_server() {

  server.on("/", []() {
    int temperature = analogRead(TMP);
    Serial.println("Temp: ");
    Serial.println(temperature);
    if (server.arg("type") == "json") {
      Serial.println("json");
      server.send(200, "text/plain", status());
    } else {
      String response;
      response = "<!DOCTYPE HTML>";
      response += "<html>";
      response += "<p>degrees C: ";
      response += temperature;
      response += "</p></html>";
      server.send(200, "text/html", response);
    }
  });

  server.on("/slow", []() {
    Serial.println("Slow");
    digitalWrite(U1, LOW);
    digitalWrite(U3, HIGH);
    server.send(200, "text/plain", status());
  });

  server.on("/normal", []() {
    Serial.println("Normal");
    digitalWrite(U1, HIGH);
    digitalWrite(U3, HIGH);
    server.send(200, "text/plain", status());
  });

  server.on("/fast", []() {
    Serial.println("fast");
    digitalWrite(U1, HIGH);
    digitalWrite(U3, LOW);
    server.send(200, "text/plain", status());
  });

  server.onNotFound(handleNotFound);
  server.begin();
  Serial.println("Server up");
}

String status() {
  String message = "";
  if (digitalRead(U1) == HIGH && digitalRead(U3) == LOW) {
    message += "2";
  }
  if (digitalRead(U1) == LOW && digitalRead(U3) == HIGH) {
    message += "0";
  }
  if (digitalRead(U1) == HIGH && digitalRead(U3) == HIGH) {
    message += "1";
  }
  return message;
}

// the setup function runs once when you press reset or power the board
void setup() {
  Serial.begin(115200);

  pinMode(U1, OUTPUT);
  pinMode(U3, OUTPUT);

  digitalWrite(U1, LOW);
  digitalWrite(U3, LOW);

  delay(10);

  Serial.print("Connecting to ");
  Serial.println(wifiName);


  WiFi.config(ip, gateway, subnet);

  WiFi.hostname(server_local_name);
  WiFi.mode(WIFI_STA);
  WiFi.begin(wifiName, wifiPass);

  digitalWrite(LED_BUILTIN, LOW);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  digitalWrite(LED_BUILTIN, HIGH);

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());   //You can get IP address assigned to ESP

  setup_server();

  digitalWrite(U1, LOW);
  digitalWrite(U3, HIGH);
}

// the loop function runs over and over again forever
void loop() {
  server.handleClient();
}
