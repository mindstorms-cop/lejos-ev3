package lejos.internal.brickpi;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import lejos.hardware.DeviceException;
import lejos.hardware.port.I2CPort;

public class BrickPiI2CPort extends BrickPiIOPort implements I2CPort {
	String caller;
    // get I2C bus instance
    private I2CBus bus;
    private I2CDevice device;
    
	public boolean open(int typ, int port, BrickPiPort ref) {
		if (!super.open(typ, port, ref)) return false;
		if (port == 4) {
			try {
				bus = I2CFactory.getInstance(I2CBus.BUS_1);
				device = bus.getDevice(0x01);
			} catch (IOException e) {
				System.err.println("Failed to create i2c bus");
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public boolean setType(int type) {
		System.out.println("Setting type to " + type);
		return true;
	}

	@Override
	public void i2cTransaction(int deviceAddress, byte[] writeBuf,
			int writeOffset, int writeLen, byte[] readBuf, int readOffset,
			int readLen) {
		if (port == 4) {
			try {
				device = bus.getDevice(deviceAddress >> 1);
				if (writeLen > 0)  {
					device.write(writeBuf, writeOffset, writeLen);
				}
				if (readLen > 0) {
					device.read(readBuf, readOffset, readLen);
				}
			} catch (IOException e) {
				throw new DeviceException("IOException from i2c", e);
			}
		} else if (readBuf != null && readBuf.length > 0 && caller != null && 
				caller.equals("lejos.hardware.sensor.NXTUltrasonicSensor")) {
			readBuf[0] = (byte) sensor.getValue();	
		}
	}}
