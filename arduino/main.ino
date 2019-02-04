uint8_t dirX;
uint8_t dirY;
#define POS HIGH
#define NEG LOW
String content = "";
int start = true;
int X_DELAY = 150;
int Y_DELAY = 150;
int TAP_DELAY = 30;
int TAP_LOOP_HIGH = 400;
int TAP_LOOP_LOW = 400;
int TAP_ONE = 0;
int TAP_TWO = 1;

#define xDirection 12
#define xStep 13
#define yDirection 10
#define yStep 11
#define xLimit 6
#define yLimit 7
#define tapDirection 8
#define tapStep 9

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
  homePen();
}

void loop() {
  if (Serial.available()) {
    char data = (char)Serial.read();
    if (data == '/') {
      int x = getValue(content, ',', 0).toInt();
      int y = getValue(content, ',', 1).toInt();
      alg(x, y);
      content = "";
    } else {
      content.concat(data);
    }
  }
}

void moveX(int dir) {
  digitalWrite(xDirection, dir);
  digitalWrite(xStep, HIGH);
  delayMicroseconds(X_DELAY);
  digitalWrite(xStep, LOW);
  delayMicroseconds(X_DELAY);
}

void homePen() {
  digitalWrite(tapDirection, 1);
  do {
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(300);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(300);
  } while (digitalRead(yLimit) == LOW);
}

void tap() {
  digitalWrite(tapDirection, TAP_ONE);
  do{
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(TAP_DELAY);
  } while(digitalRead(yLimit) == HIGH);
  digitalWrite(tapDirection, TAP_TWO);
  for(int i = 0; i < 200; i++){
    digitalWrite(tapStep, HIGH);
    delayMicroseconds(TAP_DELAY);
    digitalWrite(tapStep, LOW);
    delayMicroseconds(TAP_DELAY);
  }
}

void moveY(int dir) {
  digitalWrite(yDirection, dir);
  digitalWrite(yStep, HIGH);
  delayMicroseconds(Y_DELAY);
  digitalWrite(yStep, LOW);
  delayMicroseconds(Y_DELAY);
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
    tap();
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
