package lejos.internal.brickpi;

import lejos.hardware.DeviceException;
import lejos.hardware.port.AnalogPort;
import lejos.hardware.port.BasicMotorPort;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.IOPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.TachoMotorPort;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.BrickPiSensorConstants;

public class BrickPiPort implements Port {
    public static final int SENSOR_PORT = 0;
    public static final int MOTOR_PORT = 1;
    protected String name;
    protected int typ;
    protected int portNum;
    
    public BrickPiPort(String name, int typ, int portNum) {
        if (typ < SENSOR_PORT || typ > MOTOR_PORT)
            throw new IllegalArgumentException("Invalid port type");
        if (portNum < 0 || 
            (typ == SENSOR_PORT && portNum >= BrickPiSensorConstants.PORTS) ||
            (typ == MOTOR_PORT && portNum >= BrickPiSensorConstants.MOTORS))
            throw new IllegalArgumentException("Invalid port number");
        this.name = name;
        this.typ = typ;
        this.portNum = portNum;
    }
    
	@Override
	public String getName() {
		return name;
	}

	@Override
	public <T extends IOPort> T open(Class<T> portclass) {
        BrickPiIOPort p = null;
        switch(typ)
        {
        case SENSOR_PORT:
            if (portclass == UARTPort.class)
                p = new BrickPiUARTPort();
            else if (portclass == AnalogPort.class)
                p = new BrickPiAnalogPort();
            else if (portclass == I2CPort.class)
                p = new BrickPiI2CPort();
            break;
        case MOTOR_PORT:
            if (portclass == BasicMotorPort.class)
                p = new BrickPiMotorPort();
            else if (portclass == TachoMotorPort.class)
                p = new BrickPiMotorPort();
            // TODO: Should we also allow Encoder?
            break;
        }
        if (p == null)
            throw new IllegalArgumentException("Invalid port interface");
        if (!p.open(typ,  portNum, this))
            throw new DeviceException("unable to open port");
        return portclass.cast(p);
	}

	@Override
	public int getPortType() {
		return typ;
	}

	@Override
	public int getSensorType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void forcePortType(int typ) {
		// TODO Auto-generated method stub
		
	}

}
