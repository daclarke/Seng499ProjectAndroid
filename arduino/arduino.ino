#include <AFMotor.h>
 
AF_DCMotor motor2(2, MOTOR12_1KHZ); // create motor #2, 1KHz pwm
AF_DCMotor motor3(3, MOTOR12_1KHZ); // create motor #3, 1KHz pwm
AF_DCMotor motor4(4, MOTOR12_1KHZ); // create motor #4, 1KHz pwm

char incomingByte = 0; 

// The setup() method runs once, when the sketch starts
void setup()   {   
  setupBlueToothConnection();
    
  motor2.setSpeed(150);     // set the speed to 200/255
  motor3.setSpeed(150);     // set the speed to 200/255
  motor4.setSpeed(250);     // set the speed to 200/255
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
  motor2.run(RELEASE); 
  motor3.run(RELEASE); 
  motor4.run(RELEASE); 
  
  while(Serial.available()) {
    incomingByte = Serial.read();
    
    switch(incomingByte){
      case 'F':
        motor2.run(FORWARD); 
        motor4.run(FORWARD); 
        break;
      case 'f':
        motor2.run(RELEASE); 
        motor4.run(RELEASE); 
        break;
      case 'V':
        motor2.run(BACKWARD); 
        motor4.run(BACKWARD); 
        break;
      case 'v':
        motor2.run(RELEASE); 
        motor4.run(RELEASE);
        break;
      case 'R':
        motor2.run(FORWARD); 
        motor4.run(BACKWARD); 
        break;
      case 'r':
        motor2.run(RELEASE); 
        motor4.run(RELEASE);
        break;
      case 'L':
        motor2.run(BACKWARD); 
        motor4.run(FORWARD); 
        break;
      case 'l':
        motor2.run(RELEASE); 
        motor4.run(RELEASE);
        break;
      case 'U':
        motor3.run(FORWARD); 
        break;
      case 'u':
        motor3.run(RELEASE); 
        break;
      case 'D':
        motor3.run(BACKWARD); 
        break;
      case 'd':
        motor3.run(RELEASE); 
        break;
      default:
        motor2.run(RELEASE); 
        motor3.run(RELEASE); 
        motor4.run(RELEASE); 
        break;       
    }
  }
  delay(50);
}
