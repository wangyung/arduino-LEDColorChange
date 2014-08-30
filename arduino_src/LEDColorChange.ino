
#include <SoftwareSerial.h>

#define LED_GREEN 9
#define LED_RED   10
#define LED_BLUE  11

#define LED 13

#define RX_PIN    6
#define TX_PIN    7
#define COMMAND_BUFFER_SIZE 4

static uint8_t level = 0;
static uint8_t command[COMMAND_BUFFER_SIZE];
static uint8_t index = 0;
static uint8_t data = 0;

SoftwareSerial swSerial(RX_PIN, TX_PIN);

void setup() {
  // put your setup code here, to run once:
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_BLUE, OUTPUT);
  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);
  
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  
  swSerial.begin(9600);
  memset(command, 0, sizeof(command));
}

bool isCloseCommand() {
  return command[0] == 0 && command[1] == 0 && command[2] == 0;
}

void loop() {
    while (!swSerial.available());
    while (swSerial.available()) {
        data = swSerial.read();
        command[index++] = data;
        if (index == COMMAND_BUFFER_SIZE) break;
        delay(1);
    }
    
    Serial.print("red = ");
    Serial.println(command[0], DEC);
    Serial.print("green = ");
    Serial.println(command[1], DEC);
    Serial.print("blue = ");
    Serial.println(command[2], DEC);
    
    if (isCloseCommand()) {
        Serial.println("receive close command");
        digitalWrite(LED_RED, LOW);
        digitalWrite(LED_GREEN, LOW);
        digitalWrite(LED_BLUE, LOW);
    } else {
        analogWrite(LED_RED, command[0]);
        analogWrite(LED_GREEN, command[1]);
        analogWrite(LED_BLUE, command[2]);
    } 
    
    index = 0;
}
