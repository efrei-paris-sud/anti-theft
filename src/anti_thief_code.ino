#include <TimerOne.h>

#include <Arduino.h>
#include <TimerOne.h>
#include <SoftwareSerial.h>
SoftwareSerial mySerial(6,7); // RX, TX
// 设定SR04连接的Arduino引脚
const int TrigPin = 2; 
const int EchoPin = 3; 
const int buzzer=8;//设置控制蜂鸣器的数字IO脚

float distance; 
unsigned char flag=0;//定义变量

unsigned long  Time_Cont = 0; 
unsigned long  Time_Cont1 = 0; 
unsigned char  FlagUpData = 0;
unsigned int  FlagAlarm = 0;
unsigned int  FlagShanSuo = 0;

unsigned char JuLi=0;
unsigned char LastJuLi=0; 
unsigned char GaiBian=0;
unsigned char Flagtart=0;
unsigned char FlagUp=0;
unsigned char Rxd[10];
void setup() 
{   // 初始化串口通信及连接SR04的引脚
        Serial.begin(115200); 
        mySerial.begin(115200);
        pinMode(TrigPin, OUTPUT); 
       // 要检测引脚上输入的脉冲宽度，需要先设置为输入状态
        pinMode(EchoPin, INPUT); 
        pinMode(buzzer,OUTPUT);//设置数字IO脚模式，OUTPUT为辒出 
      //**************************
      //wifi启动
      //**************************    
         delay(1000);
          delay(1000);
          delay(1000);
          mySerial.print("AT\r\n");
          delay(1000);
          mySerial.print("AT\r\n");
          delay(1000);
          mySerial.print("AT\r\n");
          delay(1000);
          mySerial.print("AT+CWMODE=1\r\n");
          delay(1000);
          mySerial.print("AT+CWJAP=\"abc\",\"babababa\"\r\n");
          delay(1000);
         delay(1000);
          delay(1000);
         delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
         delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          /*******************************************************/
          mySerial.print("AT+CIPSTART=\"TCP\",\"192.168.43.1\",8888\r\n");
          /*******************************************************/
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          delay(1000);
          mySerial.print("AT+CIPSEND=15\r\n"); 
          delay(1000);
          mySerial.print("DanPianJiToGprs");
          delay(100);        
        Timer1.initialize(10000);
        Timer1.attachInterrupt(Timer1_handler);
        Flagtart = 1;
} 


void Timer1_handler(void)
{
        Time_Cont++;
        if(Time_Cont>=50)
        {
              Time_Cont = 0;  
              FlagUpData = 1;
        }

        Time_Cont1++;
        if(Time_Cont1>=50)
        {        
                Time_Cont1 = 0;
                if(FlagAlarm==1)
                {
                      if(FlagShanSuo==0)
                      {
                            digitalWrite(buzzer, LOW); 
                            FlagShanSuo = 1;
                      }
                      else
                      {
                             digitalWrite(buzzer, HIGH);  
                             FlagShanSuo = 0;
                       }
                      
                 }
                 else
                 {
                      digitalWrite(buzzer, LOW); 
                  }
        }

}

void loop() 
{ 
        unsigned char i,j;//定义变量
  
        // 产生一个10us的高脉冲去触发TrigPin 
        digitalWrite(TrigPin, LOW); 
        delayMicroseconds(2); 
        digitalWrite(TrigPin, HIGH); 
        delayMicroseconds(10);
        digitalWrite(TrigPin, LOW); 
        // 检测脉冲宽度，并计算出距离
        distance = pulseIn(EchoPin, HIGH) / 58.00;
        JuLi = (unsigned char)distance;
        if(Flagtart==1)
        {
              Flagtart = 0;
              LastJuLi =  JuLi;
         }
        if(FlagUpData==1)
        {
              FlagUpData = 0;
              if(JuLi>=LastJuLi) GaiBian=(JuLi-LastJuLi)*100/JuLi; 
              else               GaiBian=(LastJuLi-JuLi)*100/JuLi;
              if(GaiBian>100) GaiBian=0; 
              if(GaiBian>20)
              {
                    FlagAlarm = 1;
               }
              Serial.print("Distance=");
              Serial.print(JuLi); 
              Serial.print("cm   "); 
              Serial.print("GaiBian=");
              Serial.print(GaiBian); 
              Serial.println("%"); 
             LastJuLi =  JuLi;

        }

        if(FlagAlarm==1)
        {
                    if(FlagUp==0)
                    {
                        mySerial.print("AT+CIPSEND=2\r\n"); 
                        delay(100);
                        mySerial.print("w");
                        mySerial.print(FlagAlarm);
                        FlagUp = 1;
                    }
         }
     while(mySerial.available()>0)
     {
            Rxd[0] = Rxd[1];  
            Rxd[1] = Rxd[2];  
            Rxd[2] = Rxd[3];
            Rxd[3] = mySerial.read();            
            if( (Rxd[0]=='F')&&(Rxd[1]=='D') )
            {
                    if( (Rxd[2]=='0')&&(Rxd[3]=='0') )
                    {
                            FlagAlarm = 0;
                            FlagUp = 0;
                            mySerial.print("AT+CIPSEND=2\r\n"); 
                            delay(100);
                            mySerial.print("w");
                            mySerial.print(FlagAlarm);
                            delay(1000);
                            Flagtart= 1;
                     }
            }

     }  
        
}
