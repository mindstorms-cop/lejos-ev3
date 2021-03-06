import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.SensorConstants;
import lejos.remote.rcx.LLC;

/**
 * Shows Lego IR bytes received on the LCD.
 * 
 * Requires a Mindsensors NRLink adapter connected to
 * sensor port S1.
 * 
 * Point the RCX remote control at the NRLink and see
 * the bytes received, or use any other Lego IR source. 
 * 
 * @author Lawrie Griffiths
 *
 */
public class LLCMonitor {
	public static void main(String[] args) throws Exception {
		LLC.init(SensorPort.S1);
		
		while (Button.ESCAPE.isUp()) {
			int b = LLC.read();
			
			if (b >= 0) {
				LCD.clear();
				LCD.drawInt(b & 0xFF, 3, 0, 0);
				LCD.refresh();
				Thread.sleep(500);				
			}
		}
	}
}
