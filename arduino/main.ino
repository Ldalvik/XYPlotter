#include <QTRSensors.h>
uint8_t dirX;
uint8_t dirY;
#define POS HIGH
#define NEG LOW
#define SCROLL 4
String content = "";
int CAN_TAP = true;
int X_SPEED = 70;
int Y_SPEED = 70;
int HOME_SPEED = 50;
int TAP_DELAY = 35;
int TAP_ONE = 1;
int TAP_TWO = 0;
boolean DEBUG = false;

#define Y_STEP 9
#define Y_DIR 8
#define X_STEP 13
#define X_DIR 12
#define TAP_STEP 11
#define TAP_DIR 10
#define TAP_LIMIT 6
#define Y_LIMIT 7
#define X_LIMIT 5
#define SENSOR A0
long xsize;
long ysize;

QTRSensors qtr;

const uint8_t SensorCount = 1;
uint16_t sensorValues[SensorCount];

void setup() {
  pinMode(Y_STEP, OUTPUT);
  pinMode(Y_DIR, OUTPUT);
  pinMode(X_STEP, OUTPUT);
  pinMode(X_DIR, OUTPUT);
  pinMode(TAP_STEP, OUTPUT);
  pinMode(TAP_DIR, OUTPUT);
  pinMode(TAP_LIMIT, INPUT);
  pinMode(Y_LIMIT, INPUT);
  pinMode(X_LIMIT, INPUT);
  pinMode(SCROLL, OUTPUT);
  pinMode(SENSOR, INPUT);
  Serial.begin(115200);
  qtr.setTypeRC();
  qtr.setSensorPins((const uint8_t[]) {
    3
  }, SensorCount);
  qtr.setEmitterPin(2);

  delay(500);
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH); // turn on Arduino's LED to indicate we are in calibration mode

  for (uint16_t i = 0; i < 50; i++) {
    qtr.calibrate();
  }

  digitalWrite(LED_BUILTIN, LOW);

  qtr.calibrationOn.minimum[0] = 0;
  qtr.calibrationOn.maximum[0] = 500;

}

void loop() {
  //qtr.read(sensorValues);
  //Serial.println(sensorValues[0]);
  if (Serial.available()) {
    char data = (char) Serial.read();
    if (data == '/') {
      if (content.equals("tap")) {
        tap();
      }
      if (content.equals("home")) {
        homePen();
      }
      if (content.equals("box")) {
        box();
      }
      if (content.startsWith("EDIT:")) {
        settings(content);
      }
      if (content.startsWith("AUTO:")) {
        automatic(content);
      }
      if (content.startsWith("MANUAL:")) {
        manual(content);
      }
      if (content.equals("nextimage")) {
        nextImage();
      }
      if (content.equals("scroll")) {
        scroll();
      }
      if (content.equals("stopscroll")) {
        stopScroll();
      }
      content = "";
    } else {
      content.concat(data);
    }
  }
}

void nextImage() {
  digitalWrite(SCROLL, HIGH);
  do {
    qtr.read(sensorValues);
    //Serial.println(sensorValues[0]);
  } while (sensorValues[0] < 500);
  digitalWrite(SCROLL, LOW);
  Serial.println("STOP");
}

void scroll() {
  digitalWrite(SCROLL, HIGH);
}

void stopScroll() {
  digitalWrite(SCROLL, LOW);
}

void moveX(int dir, int frequency) {
  digitalWrite(X_DIR, dir);
  digitalWrite(X_STEP, HIGH);
  delayMicroseconds(frequency);
  digitalWrite(X_STEP, LOW);
  delayMicroseconds(frequency);
}

void moveY(int dir, int frequency) {
  digitalWrite(Y_DIR, dir);
  digitalWrite(Y_STEP, HIGH);
  delayMicroseconds(frequency);
  digitalWrite(Y_STEP, LOW);
  delayMicroseconds(frequency);
}

void homePen() {
  digitalWrite(TAP_DIR, 0);
  do {
    digitalWrite(TAP_STEP, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(TAP_STEP, LOW);
    delayMicroseconds(TAP_DELAY);
  } while (digitalRead(TAP_LIMIT) == LOW);

  do {
    if (digitalRead(X_LIMIT) == LOW) {
      moveX(0, 40);
    }
    if (digitalRead(Y_LIMIT) == LOW) {
      moveY(0, 40);
    }
  } while (digitalRead(Y_LIMIT) == LOW || digitalRead(X_LIMIT) == LOW);

  for (int i = 0; i < 27000; i++) {
    moveX(1, HOME_SPEED);
  }
}

void check() {
  do {
    if (digitalRead(X_LIMIT) == LOW) {
      moveX(0, HOME_SPEED);
    }
    if (digitalRead(Y_LIMIT) == LOW) {
      moveY(0, HOME_SPEED);
    }
  } while (digitalRead(Y_LIMIT) == LOW || digitalRead(X_LIMIT) == LOW);
  for (int i = 0; i < 28500; i++) {
    moveY(1, HOME_SPEED);
  }
  //box();
}

void tap() {
  digitalWrite(TAP_DIR, TAP_ONE);

  do {
    digitalWrite(TAP_STEP, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(TAP_STEP, LOW);
    delayMicroseconds(TAP_DELAY);
  } while (digitalRead(TAP_LIMIT) == HIGH);

  digitalWrite(TAP_DIR, TAP_TWO);

  for (int i = 0; i < 240; i++) {
    digitalWrite(TAP_STEP, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(TAP_STEP, LOW);
    delayMicroseconds(TAP_DELAY);
  }
}

void tapDown() {
  digitalWrite(TAP_DIR, TAP_ONE);
  do {
    digitalWrite(TAP_STEP, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(TAP_STEP, LOW);
    delayMicroseconds(TAP_DELAY);
  } while (digitalRead(TAP_LIMIT) == HIGH);
}

void tapUp() {
  digitalWrite(TAP_DIR, TAP_TWO);
  for (int i = 0; i < 200; i++) {
    digitalWrite(TAP_STEP, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(TAP_STEP, LOW);
    delayMicroseconds(TAP_DELAY);
  }
}

void box() {
  homePen();
  for (int i = 0; i < 29000; i++) {
    moveY(1, 40);
  }
  for (int i = 0; i < 23000; i++) {
    moveX(0, 40);
  }
  tapDown();
  for (int a = 0; a < 300 ; a++) {
    for (int x = 0; x < 150; x++) {
      moveX(0, HOME_SPEED);
    }
    for (int x = 0; x < 150; x++) {
      moveX(1, HOME_SPEED);
    }
    moveY(0, HOME_SPEED);
    moveY(0, HOME_SPEED);
    moveY(0, HOME_SPEED);
    moveY(0, HOME_SPEED);
    moveY(0, HOME_SPEED);
  }
  tapUp();
  do {
    qtr.read(sensorValues);
    Serial.println(sensorValues[0]);
    digitalWrite(SCROLL, HIGH);
  } while (sensorValues[0] < 600);
  digitalWrite(SCROLL, LOW);
  Serial.println("STOP");
}

void box2() {
  homePen();
  do {
    qtr.read(sensorValues);
    Serial.println(sensorValues[0]);
    digitalWrite(SCROLL, HIGH);
  } while (sensorValues[0] < 600);
  digitalWrite(SCROLL, LOW);
  Serial.println("STOP");
}

void settings(String data) {
  data.remove(0, 5);
  int xspeed = getValue(data, ',', 0).toInt();
  int yspeed = getValue(data, ',', 1).toInt();

  int homespeed = getValue(data, ',', 2).toInt();
  int tapdelay = getValue(data, ',', 3).toInt();

  X_SPEED = xspeed;
  Y_SPEED = yspeed;
  HOME_SPEED = homespeed;
  TAP_DELAY = tapdelay;
}

void automatic(String data) {
  data.remove(0, 5);
  int xx = getValue(data, ',', 0).toInt();
  int yy = getValue(data, ',', 1).toInt();

  if (xx <= 0) dirX = POS; else dirX = NEG;
  if (yy <= 0) dirY = POS; else dirY = NEG;
  int x = abs(xx);
  int y = abs(yy);
  if (x != 0) {
    for (int i = 0; i < x; i++) {
      moveX(dirX, X_SPEED);
    }
  }

  if (y != 0) {
    for (int i = 0; i < y; i++) {
      moveY(dirY, Y_SPEED);
    }
  }
  if (CAN_TAP) {
    tap();
  }
  Serial.print("5");
}

void manual(String data) {
  data.remove(0, 7);
  Serial.println(data);
  int xx = getValue(data, ',', 0).toInt();
  int yy = getValue(data, ',', 1).toInt();

  if (xx <= 0) dirX = POS; else dirX = NEG;
  if (yy <= 0) dirY = POS; else dirY = NEG;
  int x = abs(xx);
  int y = abs(yy);

  if (x != 0) {
    for (int i = 0; i < x; i++) {
      moveX(dirX, X_SPEED);
    }
  }
  if (y != 0) {
    for (int i = 0; i < y; i++) {
      moveY(dirY, Y_SPEED);
    }
  }

  if (DEBUG) {
    Serial.print(xx);
    Serial.print(",");
    Serial.println(yy);
  }
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
