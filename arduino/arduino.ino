//2-Way motor control

//Motor 1
int motorPin1 =  5;    
int motorPin2 =  6;    

//Motor 2
int motorPin3 =  10;
int motorPin4 =  11;

//Motor 3
int motorPin5 =  3;
int motorPin6 =  9;

int incomingByte = 0; 

int leftMotor = 0;
int rightMotor = 0;

boolean forward = false;
boolean reverse = false;
boolean right = false;
boolean left = false;
boolean up = false;
boolean down = false;

// The setup() method runs once, when the sketch starts
void setup()   {                
  
  // initialize the digital pins as an output:
  pinMode(motorPin1, OUTPUT); 
  pinMode(motorPin2, OUTPUT);  
  pinMode(motorPin3, OUTPUT); 
  pinMode(motorPin4, OUTPUT);  
  
  Serial.begin(9600);
}

// the loop() method runs over and over again,
// as long as the Arduino has power
void loop()                     
{
  digitalWrite(motorPin4, HIGH);
  digitalWrite(motorPin3, LOW);
    
  if (Serial.available() > 0) {
    incomingByte = Serial.read();
    
    if(incomingByte == 70){         //F    
        digitalWrite(motorPin2, HIGH);
        digitalWrite(motorPin1, LOW);
        //forward = true;
    } else if(incomingByte == 102){ //f
        forward = false;
    }
    if(incomingByte == 86){         //V  
        reverse = true;
    } else if(incomingByte == 118){ //v     
        reverse = false;
    } 
    
    if(incomingByte == 82){         //R      
        right = true;
    } else if(incomingByte == 114){ //r    
        right = false;
    }
    if(incomingByte == 76){         //L     
        left = true;
    } else if(incomingByte == 108){ //l   
        left = false;
    } else 
    
    if(incomingByte == 85){         //U  
        up = true;
    } else if(incomingByte == 117){ //u     
        up = false;
    }
    if(incomingByte == 68){         //D  
        down = true;
    } else if(incomingByte == 100){ //d     
        down = false;
    }
  }
  delay(10);
  
  if(forward && right && !left){
    move_right(255);
  } else if(forward && !right && left){
    move_left(255);
  } else if(forward){
    move_forward(255);
  }
  
  if(reverse && right && !left){
    move_right(255);
  } else if(reverse && !right && left){
    move_left(255);
  } else if(reverse){
    move_reverse(255);
  }  
  
  if(up){
    move_up(255);
  } else if(down){
    move_down(255);
  }
  
  rotateLeftMotor(leftMotor);
  rotateRightMotor(rightMotor);
}

void move_forward(int motorSpeed){
  rotateLeftMotor(motorSpeed);
  rotateRightMotor(motorSpeed);
}

void move_reverse(int motorSpeed){
  rotateLeftMotor(-motorSpeed);
  rotateRightMotor(-motorSpeed);
}

void move_left(int motorSpeed){
  rotateLeftMotor(-motorSpeed);
  rotateRightMotor(motorSpeed);
}

void move_right(int motorSpeed){
  rotateLeftMotor(motorSpeed);
  rotateRightMotor(-motorSpeed);
}

void move_up(int motorSpeed){
  
}

void move_down(int motorSpeed){
  
}

void rotateLeftMotor(int leftMotorSpeed){  
  if(leftMotorSpeed >= 0 && leftMotorSpeed <= 255){
    analogWrite(motorPin2, leftMotorSpeed);
    digitalWrite(motorPin1, LOW);
  }
  else if(leftMotorSpeed < 0 && leftMotorSpeed >= -255){
    analogWrite(motorPin1, -leftMotorSpeed);
    digitalWrite(motorPin2, LOW);
  }
}

void rotateRightMotor(int rightMotorSpeed){  
  if(rightMotorSpeed >= 0 && rightMotorSpeed <= 255){
    analogWrite(motorPin4, rightMotorSpeed);
    digitalWrite(motorPin3, LOW);
  }
  else if(rightMotorSpeed < 0 && rightMotorSpeed >= -255){
    analogWrite(motorPin3, -rightMotorSpeed);
    digitalWrite(motorPin4, LOW);
  }
}


