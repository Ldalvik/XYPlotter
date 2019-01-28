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
int isTapped;
int start = true;
int X_DELAY = 50;
int Y_DELAY = 50;
int TAP_DELAY = 20;
int TAP_LOOP_HIGH = 400;
int TAP_LOOP_LOW = 400;
int TAP_ONE = 0;
int TAP_TWO = 1;

#define yDirection 12
#define yStep 13
#define xDirection 10
#define xStep 11
#define xLimit 6
#define yLimit 7
#define tapDirection 8
#define tapStep 9
#define rx 5
#define tx 4
SoftwareSerial blu(rx, tx);

void setup() {
  pinMode(yDirection, OUTPUT);
  pinMode(yStep, OUTPUT);
  pinMode(xDirection, OUTPUT);
  pinMode(xStep, OUTPUT);
  pinMode(tapDirection, OUTPUT);
  pinMode(tapStep, OUTPUT);
  pinMode(xLimit, INPUT);
  pinMode(yLimit, INPUT);
  Serial.begin(115200);
  blu.begin(9600);
  homePen();
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
        delay(10);
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
      String cmd = String(content);
      if (cmd == "stop") {
        shouldMove = false;
        if (isTapped) {
          tap(TAP_TWO);
          isTapped = false;
        }
      } else if (cmd.startsWith("EDIT:")) {
        cmd.remove(0, 5);
        String command = getValue(cmd, '=', 0);
        int value = getValue(cmd, '=', 1).toInt();
        changeSettings(command, value);
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
    //Serial.println(digitalRead(xLimit));

  }
  if (digitalRead(yLimit) == HIGH) {
    //Serial.println(digitalRead(yLimit));

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
  if (data == "tap" && isTapped == false) {
    tap(TAP_ONE);
    isTapped = true;
  }
}

void moveY(int dir) {
  digitalWrite(xDirection, dir);
  digitalWrite(xStep, HIGH);
  delayMicroseconds(X_DELAY);
  digitalWrite(xStep, LOW);
  delayMicroseconds(X_DELAY);
  if (dir) {
    xcoord++;
  } else {
    xcoord--;
  }
  //Serial.println(ycoord);
}


void homePen() {
  digitalWrite(tapDirection, 1);
  int steps = 0;
  do {
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(300);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(300);
    steps++;
  } while (digitalRead(yLimit) == LOW);

  /*digitalWrite(tapDirection, 0);
    for (int i = 0; i < steps; i++) {
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(300);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(300);
    }
    digitalWrite(tapDirection, 0);
    for (int i = 0; i < steps; i++) {
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(100);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(100);
    }*/
}

void tap(int dir) {
  digitalWrite(tapDirection, dir);
  if (dir) {
    for (int i = 0; i < TAP_LOOP_HIGH; i++) {
      //do {
      digitalWrite(tapStep, HIGH);
      delayMicroseconds(TAP_DELAY);
      digitalWrite(tapStep, LOW);
      delayMicroseconds(TAP_DELAY);
    } //while (digitalRead(yLimit) == LOW);
  } else {
    for (int i = 0; i < TAP_LOOP_LOW; i++) {
      digitalWrite(tapStep, HIGH);
      delayMicroseconds(TAP_DELAY);
      digitalWrite(tapStep, LOW);
      delayMicroseconds(TAP_DELAY);
    }
  }
  if (dir) {
    xcoord++;
  } else {
    xcoord--;
  }
  //Serial.println(ycoord);
}
void moveX(int dir) {
  digitalWrite(yDirection, dir);
  digitalWrite(yStep, HIGH);
  delayMicroseconds(Y_DELAY);
  digitalWrite(yStep, LOW);
  delayMicroseconds(Y_DELAY);
  if (dir) {
    ycoord++;
  } else {
    ycoord--;
  }
  //Serial.println(ycoord);
}


void alg(int xx, int yy)
{
  if (xx <= 0) dirX = NEG; else dirX = POS;
  if (yy <= 0) dirY = POS; else dirY = NEG;
  int x = abs(xx);
  int y = abs(yy);
  if (x != 0) {
    for (int i = 0; i < x; i++) {
      moveX(dirX);
    }
  }

  if (y != 0) {
    for (int i = 0; i < y; i++) {
      moveY(dirY);
    }
  }
  if (start) {
    tap(TAP_ONE);
    tap(TAP_TWO);
  }
  Serial.print("5");
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

void changeSettings(String cmd, int value) {
  if (cmd == "X_DELAY") {
    X_DELAY = value;
    Serial.print("X_DELAY:");
    Serial.println(X_DELAY);
  }

  if (cmd == "Y_DELAY") {
    Y_DELAY = value;
    Serial.print("Y_DELAY:");
    Serial.println(Y_DELAY);
  }

  if (cmd == "TAP_DELAY") {
    TAP_DELAY = value;
    Serial.print("TAP_DELAY:");
    Serial.println(TAP_DELAY);
  }

  if (cmd == "TAP_LOOP_HIGH") {
    TAP_LOOP_HIGH = value;
    Serial.print("TAP_LOOP_HIGH:");
    Serial.println(TAP_LOOP_HIGH);
  }

  if (cmd == "TAP_LOOP_LOW") {
    TAP_LOOP_LOW = value;
    Serial.print("TAP_LOOP_LOW:");
    Serial.println(TAP_LOOP_LOW);
  }

  if (cmd == "REVERSE_TAP") {
    if (TAP_ONE) {
      TAP_ONE = 0;
      TAP_TWO = 1;
      Serial.print("TAP_ONE:");
      Serial.println(TAP_ONE);
      Serial.print("TAP_TWO:");
      Serial.println(TAP_TWO);
    } else {
      TAP_ONE = 1;
      TAP_TWO = 0;
      Serial.print("TAP_ONE:");
      Serial.println(TAP_ONE);
      Serial.print("TAP_TWO:");
      Serial.println(TAP_TWO);
    }
  }
}
