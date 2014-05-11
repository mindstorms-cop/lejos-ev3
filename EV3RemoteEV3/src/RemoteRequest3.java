import java.io.IOException;

import lejos.hardware.lcd.TextLCD;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.utility.Delay;


public class RemoteRequest3 {

	public static void main(String[] args) throws IOException {
		RemoteRequestEV3 ev3 = new RemoteRequestEV3("192.168.0.9");
		
		System.out.println("Connected");
		
		TextLCD lcd = ev3.getTextLCD();
		
		System.out.println("Got LCD");
		
		lcd.clear();
		
		System.out.println("Cleared screen");
		lcd.drawString("Hello", 4, 3);
		
		System.out.println("Drawn string");
		Delay.msDelay(2000);
		
		

	}

}
