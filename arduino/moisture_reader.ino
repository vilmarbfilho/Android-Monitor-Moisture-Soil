#define pino_sinal_analogico A0

const int seco = 0;
const int moderada = 1;
const int umido = 2;
 
int valor_analogico;
 
void setup()
{
  Serial.begin(115200);
  pinMode(pino_sinal_analogico, INPUT);
}
 
void loop()
{
  //Le o valor do pino A0 do sensor
  valor_analogico = analogRead(pino_sinal_analogico);
 
  //Solo umido, acende o led verde
  if (valor_analogico > 0 && valor_analogico < 400)
  {
    Serial.println(umido);
  }
 
  //Solo com umidade moderada, acende led amarelo
  if (valor_analogico > 400 && valor_analogico < 800)
  {
    Serial.println(moderada);
  }
 
  //Solo seco, acende led vermelho
  if (valor_analogico > 800 && valor_analogico < 1024)
  {
    Serial.println(seco);
  }
  
  delay(1000);
}
