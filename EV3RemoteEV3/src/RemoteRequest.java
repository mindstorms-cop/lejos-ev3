import java.io.IOException;

import lejos.hardware.Audio;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.LED;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.utility.Delay;

public class RemoteRequest {
	
	public static void main(String[] args) throws IOException {
	
		RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
		
		System.out.println("Voltage: " + ev3.getPower().getVoltage());
		System.out.println("Voltage (mv): " + ev3.getPower().getVoltageMilliVolt());
		System.out.println("Battery Current: " + ev3.getPower().getBatteryCurrent());
		System.out.println("Motor current: " + ev3.getPower().getMotorCurrent());
		
		Audio sound = ev3.getAudio();
		
		for(int i=0;i<5;i++) {
			sound.systemSound(i);
			Delay.msDelay(1000);
		}
		
		LED led = ev3.getLED();
		for(int i=1;i>=0;i--) {
			led.setPattern(i);
			Delay.msDelay(1000);
		}	
		
		Keys keys = ev3.getKeys();
		
		// Press keys on EV3
		System.out.println("Waiting for key press");
		int b = keys.waitForAnyEvent();
		System.out.println("Key is " + b);
		
		System.out.println("Waiting for key press");
		b = keys.waitForAnyPress();
		System.out.println("Key is " + b);
		
		b = keys.getButtons();
		System.out.println("Key is " + b);
		
		b = keys.readButtons();
		System.out.println("Key is " + b);
		
		TextLCD lcd = ev3.getTextLCD();
		lcd.clear(0, 1, 7);
		
		lcd.drawString("Hello", 2, 4);
		
		lcd.drawInt(100, 3, 5, 6);
		
		NXTMotor m = new NXTMotor(ev3.getPort("A"));
		
		m.setPower(50);
		m.forward();
		Delay.msDelay(3000);
		m.stop();
		m.close();
		
		Key right = ev3.getKey("Right");
		System.out.println("Right down: " + right.isDown());
		
		System.out.println("Waiting for key press");
		right.waitForPress();
		System.out.println("Right pressed");
		
		EV3TouchSensor touch = new EV3TouchSensor(ev3.getPort("S1"));
		
		System.out.println("Touch sensor opened");
		
		float[] sample = new float[touch.sampleSize()];
		touch.fetchSample(sample, 0);
		System.out.println("Touch: " + sample[0]);
		touch.close();
		
		NXTSoundSensor soundSensor = new NXTSoundSensor(ev3.getPort("S2"));
		float[] sample2 = new float[soundSensor.sampleSize()];
		soundSensor.fetchSample(sample2, 0);
		System.out.println("Sound: " + sample2[0]);
		soundSensor.close();
		
		EV3GyroSensor gyro = new EV3GyroSensor(ev3.getPort("S3"));
		float[] sample3 = new float[gyro.getRateMode().sampleSize()];
		System.out.println("Gyro sample size is " + gyro.getRateMode().sampleSize());
		gyro.getRateMode().fetchSample(sample3, 0);
		System.out.println("Gyro: " + sample3[0]);
		gyro.close();
		
		
		Delay.msDelay(5000);
		
		
	}

}
