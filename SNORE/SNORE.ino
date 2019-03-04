#define AMBIENT 50
#include <SPI.h>

int snore, count = 0, count2 = 0, count_3 = 0;
bool apnea = false, calibrated = false;
unsigned long t_start, t_stop, snore_score = 0, apnea_score = 0, last_score = 0;
char message[20];
char a[5], s[5];

void setup() 
{
  Serial.begin(115200);
  analogReference(DEFAULT);
}

void loop() 
{
  if(Serial.available())
  {
    Serial.println(Serial.read());
  }
  if(calibrated)
  {
    if(apnea && snore_score < last_score - 2500 && apnea_score > 7500)
    {
      apnea = false;
    }
    if(!apnea && snore_score > 9000)
    {
      apnea = true;
    }
    if(analogRead(A1) > AMBIENT)
    {
      t_start = millis();
      snore = 50;
      
      while(snore > 0)
      {
        if(analogRead(A1) > AMBIENT)
        {
          snore = snore + 1;
        }
        else
        {
          snore = snore - 2;
        }
        
        count_3++;
        if(count_3 > 700)
        {
          write_message();
          count_3 = 0;
        }
        delay(1);
        /*Serial.print("SNORE: ");
        Serial.print(snore);
        Serial.print("  ");
        Serial.println(millis() - t_start);*/
      }
      
      t_stop = millis();
      if(t_stop - t_start > 500)
      {
        snore_score += t_stop - t_start;
        if(apnea_score >= 500)
        {
          apnea_score -= 500;
        }
        if(snore_score > 10000)
        {
          snore_score = 10000;
        }
        last_score = snore_score;
      }
    }
  
    count++; 
    if(snore_score > 0 && count > 10)
    {
      snore_score -= 1;
      count = 0;
    }
    if(snore_score < last_score - 500 && last_score > 500 && apnea)
    {
      
      apnea_score += 1;
      if(apnea_score > 10000)
      {
        apnea_score = 10000;
      }
      //Serial.println(apnea_score);
    }
  
    if(!apnea && apnea_score > 0)
    {
      apnea_score -= 1;
    }
    /*Serial.print("s:");
    Serial.print(snore_score);
    Serial.print("a");
    Serial.print(apnea_score);*/
    
    /*message = "s" + snore_score;
    message += "a";
    message += apnea_score;*/

    count2++;
    if(count2 > 700)
    {
      write_message();
      count2 = 0;
    }
  }

  delay(1);
}

void write_message()
{
  Serial.print("s");
  Serial.print(snore_score);
  Serial.print("a");
  Serial.print(apnea_score);
}


