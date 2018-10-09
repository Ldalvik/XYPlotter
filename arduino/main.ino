#include <Servo.h>
#include <SoftwareSerial.h>

SoftwareSerial blu(4, 3);

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

void setup() {
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(6, INPUT);
  pinMode(7, INPUT);
  pinMode(8, INPUT);
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
        blu.print(content);
        blu.print("/");
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

  if (digitalRead(5) == HIGH) {

  }
  if (digitalRead(6) == HIGH) {

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
  digitalWrite(8, dir);
  digitalWrite(7, HIGH);
  delayMicroseconds(100);
  digitalWrite(7, LOW);
  delayMicroseconds(100);
  if (dir) {
    xcoord++;
  } else {
    xcoord--;
  }
  Serial.println(ycoord);
}

void moveY(int dir) {
  digitalWrite(6, dir);
  digitalWrite(5, HIGH);
  delayMicroseconds(10);
  digitalWrite(5, LOW);
  delayMicroseconds(100);
  if (dir) {
    ycoord++;
  } else {
    ycoord--;
  }
  Serial.println(ycoord);
}

void alg(int x, int y)
{
  deltaX = x;
  deltaY = y;

  if (deltaX <= 0) dirX = POS; else dirX = NEG;

  if (deltaY <= 0) dirY = NEG; else dirY = POS;

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
  blu.print(corX);
  blu.print(":");
  blu.println(corY);
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
