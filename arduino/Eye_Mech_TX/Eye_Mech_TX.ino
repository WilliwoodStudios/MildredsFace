#include "WiiClassy.h"
#include "Wire.h"
#include <Servo.h>

/**
 * Used to indicate that the controller is plugged into Analogue pins 2~5, 
 * using 2&3 for power.
 */
static const bool ADAPTER_IN_A2345 = false;

WiiClassy classy = WiiClassy();

void setup() 
{
  if (ADAPTER_IN_A2345) {
    digitalWrite(A3,1);
    digitalWrite(A2,0);
    pinMode(A3,OUTPUT);
    pinMode(A2,OUTPUT);
  }

  delay(100);
  classy.init();
  delay(100);
  classy.update();

  Serial.begin(38400);
}


// Left Stick :  0 ~ 63
// Right Stick: 0 ~ 31

uint8_t messageBuffer[5];

void loop() 
{
  classy.update();

  messageBuffer[0] = classy.leftStickX + '!';
  messageBuffer[1] = classy.leftStickY + '!';
  messageBuffer[2] = classy.rightStickX + '!';
  messageBuffer[3] = classy.rightStickY + '!';
  messageBuffer[4] = '\r';

  Serial.write(messageBuffer,sizeof(messageBuffer));
}
