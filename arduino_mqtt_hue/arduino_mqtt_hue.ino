#include <Ethernet.h>
#include <SPI.h>
#include <PubSubClient.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
/*
 * Created 6 Dec 2018 
 * by Sofia Larsson and Maximilan Falkenström
 */


//HUE
const char hueUsername[] = "1c4738123d9e1f373321fc2e1a934d63";
const char server[] = "192.168.20.129";          //Hue-lights
int groupID;

String status = "";

//Ethernet shield
const byte mac[] = { 0x2A, 0xF8, 0xB7, 0x5B, 0x8F, 0x97 };  //Arduino Ethernet Shield MAC-address
IPAddress ip(195, 178, 254, 88); //Arduino Shield IP-address
IPAddress myDns(192, 168, 0, 1);
EthernetClient client; //Initialize the Ethernet client library 

char inputString1[80]; // general purpose input string, 80 bytes 
char inputString2[80]; // general purpose input string, 80 bytes 
char outputString[80]; // general output string, 80 bytes 

//MQTT
const char user[] = "dlevwepg";           //MQTT username
const char password[] = "fYRxSnoN2Ll9";    //MQTT password
const char host[] = "m21.cloudmqtt.com";  //MQTT-server URL
const int port = 14873; 
PubSubClient mqttClient(client);

void setup() {
  Ethernet.init(10);  //Configure the CS pin (pin 10 for most Arduino Shields)

  Serial.begin(9600); //data rate in bits per second for data transmission
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // start the Ethernet connection:
  //Serial.println(F("Initialize Ethernet with DHCP:"));
  if (Ethernet.begin(mac) == 0) {
    //Serial.println(F("Failed to configure Ethernet using DHCP"));
    // Check for Ethernet hardware present
    if (Ethernet.hardwareStatus() == EthernetNoHardware) {
      //Serial.println(F("Ethernet shield was not found.  Sorry, can't run without hardware. :("));
      while (true) {
        delay(1); // do nothing, no point running without Ethernet hardware
      }
    }
    if (Ethernet.linkStatus() == LinkOFF) {
      //Serial.println(F("Ethernet cable is not connected."));
    }
    // try to congifure using IP address instead of DHCP:
    Ethernet.begin(mac, ip, myDns);
  } else {
    //Serial.print(F("  DHCP assigned IP "));
    Serial.println(Ethernet.localIP());
  }
  // give the Ethernet shield a second to initialize:
  delay(2000);
  //Serial.print(F("connecting to "));
  //Serial.print(server);
  //Serial.println(F("..."));

  mqttClient.setServer(host, port);
  mqttClient.setCallback(callback);
  
  delay(1000);
  setBrightness(50, 3, false);
  delay(1000);
  setHue(1000L, 3, false);
  delay(1000);
  setPower(true, 3, false);
  delay(2000);
  
  groupID = createGroup();

  delay(5000);
}

void loop() {
  /*String status1 = getStatus(1);
  String status2 = getStatus(2);
  String status3 = getStatus(3);
  
  status = status1;
  status += ";";
  status += status2;
  status += ";";
  status += status3;
  Serial.println(status);*/

  const char s[status.length()+1];
  status.toCharArray(s, status.length()+1);
  
  if(!mqttClient.connected())
  {
    connectToMQTT();
  }
  
  //boolean mqttMessageSent = mqttClient.publish("fromHue", s, true);
  /*
  if(mqttMessageSent)
  {
    Serial.println(F("Successfully sent via MQTT"));
  }
  else
  {
    Serial.println(F("Failed to send via MQTT"));
  }*/
  
  mqttClient.loop();
}

void callback(char* topic, byte* payload, unsigned int length) {
  String str = payload;
  Serial.println(str);
  int lampId = splitString(str, ',', 0).toInt();
  String operation = splitString(str, ',', 1);
  long value = atol(splitString(str, ',', 2).c_str()); 
  boolean group = false; 
  Serial.println(String(value) + " " + String(lampId) + " " + operation);
  if(lampId == 4) {
    group = true; 
    lampId = groupID;
  } 
  if(operation.equals("power")){
    setPower(value, lampId, group);
    /*if(value == 1) {
      setPower(true, lampId, group);
    } else {
      setPower(false, lampId, group);
    }*/
  } else if(operation.equals("brightness")) {
    setBrightness(value, lampId, group);
  } else if(operation.equals("hue")) {
    setHue(value, lampId, group);
  }
  /*
  for (int i=0;i<length;i++) {
    char receivedChar = (char)payload[i];
  }*/
  
}

boolean connectToMQTT()
{
  const char clientID[] = "Arduino MQTT";     //ID to use for MQTT
  const char user[] = "dlevwepg";           //MQTT username
  const char password[]= "fYRxSnoM2Ll9";    //MQTT password
  
  while(!mqttClient.connected())
  {
    Serial.println(F("connecting to mqtt"));
    if(mqttClient.connect(clientID, user, password))
    {
      mqttClient.subscribe("toHue");
    }
    else
    {
      delay(5000);
    }
  }
}

String splitString(String str, char separator, int index) {
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = str.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (str.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? str.substring(strIndex[0], strIndex[1]) : "";
}

const int createGroup() {
    int id;
    if(client.connect(server, 80)) {
      char body[] = "{\"lights\": [\"1\",\"2\",\"3\"],\"name\":\"testgroup\"}";
      client.print("POST /api/");
      client.print(hueUsername);
      client.print("/groups");
      client.println(" HTTP/1.1");
      client.println("keep-alive");
      client.print("Host: ");
      client.println(server);
      client.println("Content-Type: application/json");
      client.print("Content-Length: ");
      client.println(strlen(body) + 1);
      client.println();
      client.println(body); 
      client.println();
      while(client.connected()) {
        if(client.available()) {
          client.findUntil("groups/", '\0');
          char idBuffer[1];
          client.readBytesUntil('\"', idBuffer, 1);
          id = atoi(idBuffer);
          break;
        }
      }
    }else {
      Serial.println(F("Create group: connection failed"));
    }
    
    client.flush();
    client.stop();
  return id;
}

String getStatus(int id) {
  String res;
  if(client.connect(server, 80)) {
    client.print("GET /api/");
    client.print(hueUsername);
    client.print("/lights/");
    client.print(id);
    client.println(" HTTP/1.1");
    client.println("keep-alive");
    client.print("Host: ");
    client.println(server);
    client.println("Content-Type: application/json");
    client.println();

    while(client.connected())
    {
      if(client.available())
      {
        client.findUntil("\"on\":", '\0');
        String on = client.readStringUntil(',') == "true" ? "1" : "0";
    
        client.findUntil("\"bri\":", '\0');
        String brightness = client.readStringUntil(',');
    
        client.findUntil("\"hue\":", '\0');
        String hue = client.readStringUntil(',');

        res = on;
        res += ",";
        res += brightness;
        res += ",";
        res += hue;

        break;
      }
    }
  }else {
    // if you didn't get a connection to the server:
    Serial.println(F("Get status: connection failed"));
  }

  client.flush();
  client.stop();

  return res;
}

void setHue(long hue, int id, bool group) {
  if(client.connect(server, 80)) {
    Serial.println(F("Set hue"));
    client.print("PUT /api/");
    client.print(hueUsername);

    if(group) {
      client.print("/groups/");
    }
    else {
      client.print("/lights/");
    }
    client.print(id);  // hueLight zero based, add 1
    client.println("/state HTTP/1.1");
    client.println("keep-alive");
    client.print("Host: ");
    client.println(server);
    client.println("Content-Length: 13");
    client.println("Content-Type: text/plain;charset=UTF-8");
    client.println();  // blank line before body
    client.println("{\"hue\": " + String(hue) + "}");  // Hue command
    client.flush();
    client.stop();
  } else {
    // if you didn't get a connection to the server:
    Serial.println(F("Set hue: connection failed"));
  }
}

void setBrightness(int brightness, int id, bool group) {
  if(client.connect(server, 80)) {
    Serial.println(F("Set brightness"));
    client.print("PUT /api/");
    client.print(hueUsername);

    if(group) {
      client.print("/groups/");
    }
    else {
      client.print("/lights/");
    }
    client.print(id);  // hueLight zero based, add 1
    client.println("/state HTTP/1.1");
    client.println("keep-alive");
    client.print("Host: ");
    client.println(server);
    client.println("Content-Length: 13");
    client.println("Content-Type: text/plain;charset=UTF-8");
    client.println();  // blank line before body
    client.println("{\"bri\": " + String(brightness) + "}");  // Hue command
    client.flush();
    client.stop();
  }else {
    // if you didn't get a connection to the server:
    Serial.println(F("Set brigness: connection failed"));
  }
}

void setPower(bool on, int id, bool group) {
  if(client.connect(server, 80)) {
    Serial.println("Set power");
    client.print("PUT /api/");
    client.print(hueUsername);

    if(group) {
      client.print("/groups/");
    }
    else {
      client.print("/lights/");
    }
    
    client.print(id);  // hueLight zero based, add 1
    client.println("/state HTTP/1.1");
    client.println("keep-alive");
    client.print("Host: ");
    client.println(server);
    client.println("Content-Length: 14");
    client.println("Content-Type: text/plain;charset=UTF-8");
    client.println();  // blank line before body
    if(on) {
      client.println("{\"on\": true}");  // Hue command
    }
    else {
      client.println("{\"on\": false}");  // Hue command
    }
    
    client.flush();
    client.stop();
  } else {
    // if you didn't get a connection to the server:
    Serial.println(F("Set power: connection failed"));
  }
}
/* setHue() is our main command function, which needs to be passed a light number and a 
 * properly formatted command string in JSON format (basically a Javascript style array of variables
 * and values. It then makes a simple HTTP PUT request to the Bridge at the IP specified at the start.
 */
boolean setHue(int lightNum, const char *command, boolean group)
{
  in commandLength = strlen(command);
  if (client.connect(server, 80))
  {
    while (client.connected())
    {
      client.print("PUT /api/");
      client.print(hueUsername);
      if(group) {
        client.print("/groups/");
      }else {
        client.print("/lights/");
      }
      client.print(lightNum);  // hueLight zero based, add 1
      client.println("/state HTTP/1.1");
      client.println("keep-alive");
      client.print("Host: ");
      client.println(server);
      client.print("Content-Length: ");
      client.println(commandLength);
      client.println("Content-Type: text/plain;charset=UTF-8");
      client.println();  // blank line before body
      client.println(command);  // Hue command
    }
    client.stop();
    return true;  // command executed
  }
  else
    return false;  // command failed
}
