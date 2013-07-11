int rx_pin = 0;     // setting digital pin 0 to be the receiving pin
int tx_pin = 1;     // setting the digital pin 1 to be the transmitting pin

//Motor 1 LEFT MOTOR
int motorPin1 =  5;    
int motorPin2 =  6;    

//Motor 2 RIGHT MOTOR
int motorPin3 =  11;
int motorPin4 =  10;

//Motor 3
int motorPin5 =  3;
int motorPin6 =  9;

char incomingByte = 0; 

// The setup() method runs once, when the sketch starts
void setup()   {                
  
  // initialize the digital pins as an output:
  pinMode(motorPin1, OUTPUT); 
  pinMode(motorPin2, OUTPUT);  
  pinMode(motorPin3, OUTPUT); 
  pinMode(motorPin4, OUTPUT);  
  pinMode(motorPin5, OUTPUT);  
  pinMode(motorPin6, OUTPUT);
  
  digitalWrite(motorPin1, LOW);
  digitalWrite(motorPin2, LOW);
  digitalWrite(motorPin3, LOW);
  digitalWrite(motorPin4, LOW);
  digitalWrite(motorPin5, LOW);
  digitalWrite(motorPin6, LOW);
  delay(100);
  
  pinMode(rx_pin, INPUT);  // receiving pin as INPUT
  pinMode(tx_pin, OUTPUT);
  setupBlueToothConnection();
}

void setupBlueToothConnection()
{
   Serial.begin(115200);
   Serial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
   Serial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
   Serial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
   Serial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
   delay(2000); // This delay is required.
   Serial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
   Serial.println("The slave bluetooth is inquirable!");
   delay(2000); // This delay is required.
   Serial.flush();
}

// the loop() method runs over and over again,
// as long as the Arduino has power
void loop()                     
{
  
  while(Serial.available()) {
    incomingByte = Serial.read();
    
    switch(incomingByte){
      case 'F':
        digitalWrite(motorPin1, HIGH);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, HIGH);
        digitalWrite(motorPin4, LOW);
        break;
      case 'f':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, LOW);
        break;
      case 'V':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, HIGH);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, HIGH);
        break;
      case 'v':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, LOW);
        break;
      case 'R':
        digitalWrite(motorPin1, HIGH);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, HIGH);
        break;
      case 'r':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, LOW);
        break;
      case 'L':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, HIGH);
        digitalWrite(motorPin3, HIGH);
        digitalWrite(motorPin4, LOW);
        break;
      case 'l':
        digitalWrite(motorPin1, LOW);
        digitalWrite(motorPin2, LOW);
        digitalWrite(motorPin3, LOW);
        digitalWrite(motorPin4, LOW);
        break;
      case 'U':
        digitalWrite(motorPin5, LOW);
        digitalWrite(motorPin6, HIGH);
        break;
      case 'u':
        digitalWrite(motorPin5, LOW);
        digitalWrite(motorPin6, LOW);
        break;
      case 'D':
        digitalWrite(motorPin5, HIGH);
        digitalWrite(motorPin6, LOW);
        break;
      case 'd':
        digitalWrite(motorPin5, LOW);
        digitalWrite(motorPin6, LOW);
        break;
      default:
        break;       
    }
  }
  delay(10);
}
