import firebase_admin
import pigpio
import time
import RPi.GPIO as GPIO
import dht11
from time import *
from RPLCD import CharLCD
from RPi import GPIO
from datetime import datetime
from time import sleep, mktime

GPIO.setwarnings(False)
cred = firebase_admin.credentials.Certificate('serviceAccountKey.json')
default_app = firebase_admin.initialize_app(cred, {'databaseURL': 'https://voice-assistant-d256f-default-rtdb.firebaseio.com'})
from firebase_admin import db

task_ref = db.reference("/Task/")
message = task_ref.get()
GPIO_PIN = 21
lcd = CharLCD(numbering_mode=GPIO.BOARD, cols=16, rows=2, pin_rs=37, pin_e=35, pins_data=[33, 31, 29, 23])
pi = pigpio.pi()

pi.set_mode(GPIO_PIN, pigpio.OUTPUT)
print (message)
while message!= "stop":
  if message == "lights on":
     pi.write(GPIO_PIN,1)
  if message == "lights off":
     pi.write(GPIO_PIN,0)
  if message == "show date and time":
     GPIO.setwarnings(False)
     dti = mktime(datetime.now().timetuple())
     message2=message
     while message==message2:
       ndti = mktime(datetime.now().timetuple())
       if dti < ndti:
          dti = ndti
          lcd.clear()
          lcd.write_string(datetime.now().strftime('%b %d  %H:%M:%S\n'))
          sleep(0.95)
          message2 = task_ref.get()
       else:
          lcd.clear()
  if message == "temperature":
     GPIO.cleanup() 
     GPIO.setwarnings(False)
     GPIO.setmode(GPIO.BCM)
     instance = dht11.DHT11(pin = 16)
     result = instance.read()
     message2 = task_ref.get()
     mode=GPIO.getmode()
     if result.is_valid() and message == message2 or mode == 11 :
      GPIO.cleanup()
      lcd = CharLCD(numbering_mode=GPIO.BOARD, cols=16, rows=2, pin_rs=37, pin_e=35, pins_data=[33, 31, 29, 23])
      lcd.write_string("Temp: %-3.1f C" % result.temperature)
  message2 = task_ref.get()
  if message2 != message:
     message = message2
     print(message2)
  if message2 != "show date and time" and message2!= "temperature":
     lcd.clear()
     lcd.write_string(message)
  if message != "stop" and message !="lights on" and message !="lights off" and message !="show date and time" and message != "temperature":
     lcd.write_string(" is not a command") 

lcd.write_string(" bye bye") 
