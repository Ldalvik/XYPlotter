#include <SoftwareSerial.h>

int16_t deltaX;
int16_t deltaY;
uint8_t dirX;
uint8_t dirY;
int16_t endCnt;
#define POS HIGH
#define NEG LOW
int currentX = 0;
int currentY = 0;
int yDirection;
int xDirection;
int16_t corX;
int16_t corY;
int delta1;
int delta2;
int error;
String content = "";
String moveType = "";
bool shouldMove = false;
int xcoord;
int ycoord;
int delayCount;
#define yDirection 6
#define xDirection 8
#define yStep 5
#define xStep 7
#define xLimit 0
#define yLimit 1
#define tap 2
#define rx 4
#define tx 3

SoftwareSerial blu(rx, tx);

void setup() {
  pinMode(yDirection, OUTPUT);
  pinMode(yStep, OUTPUT);
  pinMode(xDirection, OUTPUT);
  pinMode(xStep, OUTPUT);
  pinMode(xLimit, INPUT);
  pinMode(yLimit, INPUT);
  pinMode(tap, OUTPUT);
  Serial.begin(9600);
  blu.begin(9600);
}

void loop() {
  int count;
  if (Serial.available()) {
    char data = (char)Serial.read();
    if (data == '/') {
      int x = getValue(content, ',', 0).toInt();
      int y = getValue(content, ',', 1).toInt();
      if (content.startsWith("B")) {
        //delayCount++;
        //if (delayCount > 10) {
          blu.print(content);
          blu.print("/");
          delay(10);
          //delayCount = 0;
        //}
      } else {
        alg(x, y);
      }
      content = "";
      count++;
    } else {
      content.concat(data);
    }
  }

  if (blu.available()) {
    char data = (char)blu.read();
    if (data == '/') {
      Serial.println(content);
      if (String(content) == "stop") {
        shouldMove = false;
      } else {
        shouldMove = true;
        moveType = String(content);
        Serial.println("moving");
      }
      content = "";
    } else {
      content.concat(data);
    }
  }

  if (shouldMove) {
    phone(moveType);
  }

  if (digitalRead(xLimit) == HIGH) {

  }
  if (digitalRead(yLimit) == HIGH) {

  }
}

void phone(String data) {
  if (data == "up") {
    moveY(1);
  }
  if (data == "down") {
    moveY(0);
  }

  if (data == "left") {
    moveX(0);
  }
  if (data == "right") {
    moveX(1);
  }

  if (data == "upRight") {
    moveX(1);
    moveY(1);
  }
  if (data == "downRight") {
    moveX(1);
    moveY(0);
  }
  if (data == "upLeft") {
    moveX(0);
    moveY(1);
  }
  if (data == "downLeft") {
    moveX(0);
    moveY(0);
  }
  if (data == "tap") {
    Serial.print(xcoord);
    Serial.println(ycoord);
  }
}

void moveX(int dir) {
  digitalWrite(xDirection, dir);
  digitalWrite(xStep, HIGH);
  delayMicroseconds(100);
  digitalWrite(xStep, LOW);
  delayMicroseconds(100);
  if (dir) {
    xcoord++;
  } else {
    xcoord--;
  }
  //Serial.println(ycoord);
}

void moveY(int dir) {
  digitalWrite(yDirection, dir);
  digitalWrite(yStep, HIGH);
  delayMicroseconds(10);
  digitalWrite(yStep, LOW);
  delayMicroseconds(100);
  if (dir) {
    ycoord++;
  } else {
    ycoord--;
  }
  //Serial.println(ycoord);
}

void alg(int x, int y)
{
  deltaX = x;
  deltaY = y;

  if (deltaX <= 0) dirX = POS; else dirX = NEG;

  if (deltaY <= 0) dirY = POS; else dirY = NEG;

  if (abs(deltaX) >= abs(deltaY)) {
    endCnt = abs(x);
    delta1 = deltaX;
    delta2 = deltaY;
  } else {
    endCnt = abs(y);
    delta1 = deltaY;
    delta2 = deltaX;
  }

  for (int z = 0; z < endCnt; z++) {
    if (abs(deltaX) >= abs(deltaY)) {
      moveX(dirX);
      corX++;
    } else {
      moveY(dirY);
      corY++;
    }

    error = abs(error + abs(delta2));
    if ((2 * error) >= abs(delta1)) {
      error = error - abs(delta1);
      if (abs(deltaX) >= abs(deltaY)) {
        moveY(dirY);
        corY++;
      } else {
        moveX(dirX);
        corX++;
      }
    }
  }
  Serial.print("5");
  //blu.print(corX);
  //blu.print(":");
  //blu.println(corY);
  corX = 0;
  corY = 0;
}

String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
