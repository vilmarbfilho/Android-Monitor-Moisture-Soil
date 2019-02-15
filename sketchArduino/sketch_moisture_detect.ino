#define pin_analogic_signal A0

const int just_dry = 0;
const int moderate_humidity = 1;
const int moist_soil = 2;

int value_analogic;

void setup() {
  Serial.begin(9600);
  pinMode(pin_analogic_signal, INPUT);
}

void loop() {
  value_analogic = analogRead(pin_analogic_signal);

  if (value_analogic > 0 && value_analogic < 400)
  {
    Serial.print(just_dry);
  }

  if (value_analogic > 400 && value_analogic < 800)
  {
    Serial.print(moderate_humidity);
  }

  if (value_analogic > 800 && value_analogic < 1024)
  {
    Serial.print(moist_soil);
  }

  delay(200);
}
