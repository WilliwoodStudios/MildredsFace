#include "Wire.h"
#include "WiiClassy.h"
#include <Servo.h>

WiiClassy classy = WiiClassy();

Servo servo1;
Servo servo2;
Servo servo3;
Servo servo4;

int spd = 10;

void setup() 
{
  delay(100);
  classy.init();
  delay(100);
  classy.update();

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

void loop() 
{
  classy.update();

  servo1.write(map(classy.leftStickX, 0, 63, xMin, xMax));
  servo2.write(map(classy.leftStickY, 0, 63, yMin, yMax));
  servo3.write(map(classy.rightStickX, 0, 31, xMin, xMax));
  servo4.write(map(classy.rightStickY, 0, 31, yMin, yMax));
}
