import lejos.hardware.LED;
import lejos.hardware.brickpi.BrickPi;
import lejos.hardware.brickpi.LocalBrickPi;
import lejos.utility.Delay;

public class LEDTest {
	
	public static void main(String[] args) {
		BrickPi pi = LocalBrickPi.get();	
		LED led = pi.getLED();
		
		for(int i=3;i>=0;i--) {
			led.setPattern(i);
			Delay.msDelay(1000);
		}
	}
}
