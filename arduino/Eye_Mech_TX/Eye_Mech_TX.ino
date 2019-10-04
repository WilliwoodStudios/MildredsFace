#include "Wire.h"
#include "WiiClassy.h"
#include "SoftwareSerial.h"
#include <Servo.h>

WiiClassy classy = WiiClassy();
SoftwareSerial softwareSerial(7,8);

int spd = 10;

void setup() 
{
  delay(100);
  classy.init();
  delay(100);
  classy.update();

  softwareSerial.begin(9600);
}


// Left Stick :  0 ~ 63
// Right Stick: 0 ~ 31

int xMin = 30;
int xMax = 120;

int yMin = 12;
int yMax = 162;

void loop() 
{
  classy.update();

  uint8_t messageBuffer[5];
  messageBuffer[0] = classy.leftStickX + '!';
  messageBuffer[1] = classy.leftStickY + '!';
  messageBuffer[2] = classy.rightStickX + '!';
  messageBuffer[3] = classy.rightStickY + '!';
  messageBuffer[4] = ' ';

  softwareSerial.write(messageBuffer,sizeof(messageBuffer));
}
