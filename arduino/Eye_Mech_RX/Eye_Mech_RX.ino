#include "Wire.h"
#include "WiiClassy.h"
#include <Servo.h>
#include "SoftwareSerial.h"

SoftwareSerial softwareSerial(7,8);
Servo servo1;
Servo servo2;
Servo servo3;
Servo servo4;

void setup() 
{
  softwareSerial.begin(9600);
  servo1.attach(3);
  servo2.attach(5);
  servo3.attach(6);
  servo4.attach(9);
}


// Left Stick :  0 ~ 63   =>   0 ~ 180

int xMin = 30;
int xMax = 120;

int yMin = 12;
int yMax = 162;

static const uint8_t PACKET_SIZE = 5;
uint8_t messageBuffer[PACKET_SIZE];
int messageBufferIndex = 0;

void loop() 
{
  uint8_t nextChar = softwareSerial.read();
  messageBuffer[messageBufferIndex] = nextChar;
  ++messageBufferIndex;
  messageBufferIndex %= PACKET_SIZE;
  if (nextChar == ' ' && messageBufferIndex == 0) {
    servo1.write(map(messageBuffer[0]-'!', 0, 63, xMin, xMax));
    servo2.write(map(messageBuffer[1]-'!', 0, 63, yMin, yMax));
    servo3.write(map(messageBuffer[2]-'!', 0, 31, xMin, xMax));
    servo4.write(map(messageBuffer[3]-'!', 0, 31, yMin, yMax));
  } else if (nextChar == ' ') {
    messageBufferIndex = 0;
  }
}
