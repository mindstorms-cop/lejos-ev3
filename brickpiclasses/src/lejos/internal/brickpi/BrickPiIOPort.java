package lejos.internal.brickpi;

import lejos.hardware.port.BasicSensorPort;
import lejos.hardware.port.IOPort;
import lejos.hardware.sensor.BrickPiSensorConstants;

import com.ergotech.brickpi.sensors.Sensor;

public class BrickPiIOPort implements IOPort, BasicSensorPort, BrickPiSensorConstants {
	protected Sensor sensor;
    protected int port = -1;
    protected int typ = -1;
    protected BrickPiPort ref;
    protected static byte [] dc = new byte[3*PORTS];
    protected int currentMode = 0;
	protected static BrickPiIOPort [][] openPorts = new BrickPiIOPort[BrickPiPort.MOTOR_PORT+1][PORTS];

    /**
     * Open the sensor port. Ensure that the port is only opened once.
     * @param typ The type of port motor/sensor
     * @param port the port number
     * @param ref the Port ref for this port
     * @return
     */
	public boolean open(int typ, int port, BrickPiPort ref) {
        synchronized (openPorts) {
            if (openPorts[typ][port] == null) {
                openPorts[typ][port] = this;
                this.port = port;
                this.typ = typ;
                this.ref = ref;
                return true;
            }
            return false;
        }
	}

	@Override
	public int getMode() {
		return currentMode;
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public boolean setMode(int mode) {
		currentMode = mode;
        return true;
	}

	@Override
	public boolean setType(int type) {
		throw new UnsupportedOperationException("This operation is for legacy modes only");
	}

	@Override
	public boolean setTypeAndMode(int type, int mode) {
        setType(type);
        setMode(mode);
        return true;
	}

	@Override
	public void close() {
        if (port == -1)
            throw new IllegalStateException("Port is not open");
        synchronized (openPorts)
        {
            openPorts[typ][port] = null;
            port = -1;
        }
	}

	@Override
	public String getName() {
		return ref.getName();
	}

	@Override
	public void setPinMode(int mode) {
		// Not yet implemented
	}
}
