package lejos.hardware.brickpi;

import java.util.ArrayList;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Key;
import lejos.hardware.Keys;
import lejos.hardware.LED;
import lejos.hardware.LocalBTDevice;
import lejos.hardware.LocalWifiDevice;
import lejos.hardware.Power;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.internal.brickpi.BrickPiAudio;
import lejos.internal.brickpi.BrickPiLED;
import lejos.internal.brickpi.BrickPiPort;

public class LocalBrickPi implements BrickPi {
	public static final LocalBrickPi brickPi = new LocalBrickPi();
	
    protected ArrayList<BrickPiPort> ports = new ArrayList<BrickPiPort>();
	
	private LocalBrickPi() {
        // Create the port objects
        ports.add(new BrickPiPort("S1", BrickPiPort.SENSOR_PORT, 0));
        ports.add(new BrickPiPort("S2", BrickPiPort.SENSOR_PORT, 1));
        ports.add(new BrickPiPort("S3", BrickPiPort.SENSOR_PORT, 2));
        ports.add(new BrickPiPort("S4", BrickPiPort.SENSOR_PORT, 3));
        ports.add(new BrickPiPort("S5", BrickPiPort.SENSOR_PORT, 4));
        ports.add(new BrickPiPort("A", BrickPiPort.MOTOR_PORT, 0));
        ports.add(new BrickPiPort("B", BrickPiPort.MOTOR_PORT, 1));
        ports.add(new BrickPiPort("C", BrickPiPort.MOTOR_PORT, 2));
        ports.add(new BrickPiPort("D", BrickPiPort.MOTOR_PORT, 3));
	}
	
    public static BrickPi get()
    {
        return brickPi;
    }

	@Override
	public Port getPort(String portName) {
        for(BrickPiPort p : ports)
            if (p.getName().equals(portName))
                return p;
        throw new IllegalArgumentException("No such port " + portName);
	}

	@Override
	public Power getPower() {
		throw new UnsupportedOperationException("Power not supported");
	}

	@Override
	public Audio getAudio() {
		return new BrickPiAudio();
	}

	@Override
	public TextLCD getTextLCD() {
		throw new UnsupportedOperationException("LCD not supported");
	}

	@Override
	public TextLCD getTextLCD(Font f) {
		throw new UnsupportedOperationException("LCD not supported");
	}

	@Override
	public GraphicsLCD getGraphicsLCD() {
		throw new UnsupportedOperationException("LCD not supported");
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public String getType() {
		return "BrickPi";
	}

	@Override
	public String getName() {
		// Not yet implemented
		return null;
	}

	@Override
	public LocalBTDevice getBluetoothDevice() {
		throw new UnsupportedOperationException("Bluetooth not supported");
	}

	@Override
	public LocalWifiDevice getWifiDevice() {
		throw new UnsupportedOperationException("Wifi not supported");
	}

	@Override
	public void setDefault() {
		BrickFinder.setDefault(this);
	}

	@Override
	public Keys getKeys() {
		throw new UnsupportedOperationException("Keys not supported");
	}

	@Override
	public Key getKey(String name) {
		throw new UnsupportedOperationException("LCD not supported");
	}

	@Override
	public LED getLED() {
		return new BrickPiLED();
	}
}
