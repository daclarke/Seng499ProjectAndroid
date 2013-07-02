//2-Way motor control

int motorPin1 =  5;    // One motor wire connected to digital pin 5
int motorPin2 =  6;    // One motor wire connected to digital pin 6
int motorPin3 =  10;
int motorPin4 =  11;

int incomingByte = 0; 

int leftMotor = 0;
int rightMotor = 0;

// The setup() method runs once, when the sketch starts
void setup()   {                
  
  // initialize the digital pins as an output:
  pinMode(motorPin1, OUTPUT); 
  pinMode(motorPin2, OUTPUT);  
  pinMode(motorPin3, OUTPUT); 
  pinMode(motorPin4, OUTPUT);  
  
  Serial.begin(115200);
}

// the loop() method runs over and over again,
// as long as the Arduino has power
void loop()                     
{
    
  if (Serial.available() > 0) {
    incomingByte = Serial.read();
    
    Serial.write(incomingByte);
    
    if(incomingByte == 70){ //F
        leftMotor += 150;
        rightMotor += 150;
    } else if(incomingByte == 102){ //f
        leftMotor -= 150;
        rightMotor -= 150;
    } else if(incomingByte == 82){ //R      
        leftMotor += 100;
        rightMotor -= 100;
    } else if(incomingByte == 114){ //r    
        leftMotor -= 100;
        rightMotor += 100;
    } else if(incomingByte == 76){ //L     
        leftMotor -= 100;
        rightMotor += 100;
    } else if(incomingByte == 108){ //l   
        leftMotor += 100;
        rightMotor -= 100;
    } else if(incomingByte == 86){ //V  
        leftMotor -= 150;
        rightMotor -= 150;
    } else if(incomingByte == 118){ //v     
        leftMotor += 150;
        rightMotor += 150;
    }
  }
  
  if(rightMotor > 255){
    rightMotor = 250;
  } else if(rightMotor < -255) {
    rightMotor = -250;
  }
  if(leftMotor > 255) {
    leftMotor = 250;
  } else if(leftMotor < -255) {
    leftMotor = -250;
  }
  
  delay(100);
  rotateLeftMotor(leftMotor);
  rotateRightMotor(rightMotor);
}

void rotateLeftMotor(int leftMotorSpeed){  
  if(leftMotorSpeed >= 0 && leftMotorSpeed < 255){
    analogWrite(motorPin2, leftMotorSpeed);
    digitalWrite(motorPin1, LOW);
  }
  else if(leftMotorSpeed < 0 && leftMotorSpeed > -255){
    analogWrite(motorPin1, -leftMotorSpeed);
    digitalWrite(motorPin2, LOW);
  }
}

void rotateRightMotor(int rightMotorSpeed){  
  if(rightMotorSpeed >= 0 && rightMotorSpeed < 255){
    analogWrite(motorPin4, rightMotorSpeed);
    digitalWrite(motorPin3, LOW);
  }
  else if(rightMotorSpeed < 0 && rightMotorSpeed > -255){
    analogWrite(motorPin3, -rightMotorSpeed);
    digitalWrite(motorPin4, LOW);
  }
}


