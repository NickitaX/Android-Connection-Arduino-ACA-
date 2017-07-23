#include <LiquidCrystal.h>
#include <Servo.h>

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
void setup() {
        Serial.begin(115200);     // opens serial port, sets data rate to 9600 bps
        lcd.begin(16, 2);
        lcd.print("Waiting ...");
}

void loop() {
        if (Serial.available() > 0) {
                lcd.print(Serial.readString());
        }
}
